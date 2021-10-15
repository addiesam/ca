import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Calc {

	private final MathContext mc;

	public Calc(final int scale) {
		mc = new MathContext(scale, RoundingMode.HALF_UP);
	}

	// Process the parentheses.
	public String calcP(final StringBuffer sb) {
		int q = 0;
		final StringBuffer sb_s = new StringBuffer();
		StringBuffer sb_q = new StringBuffer();
		StringBuffer sb_p = sb_s;

		for (int i = 0; i < sb.length(); i++) {
			final char c = sb.charAt(i);

			if ((c == ' ') || (c == ',')) {
				continue;
			}

			if (c == '(') {
				q++;
				if (q == 1) {
					sb_q = new StringBuffer();
					sb_p = sb_q;
					continue;
				}
			}

			if (c == ')') {
				q--;
				if (q == 0) {
					sb_s.append(calcP(sb_q));
					sb_p = sb_s;
					continue;
				}
			}

			sb_p.append(c);
		}

		return (calcO(sb_s.toString()).toPlainString());
	}

	// Process the operators.
	public BigDecimal calcO(final String s) {
		int op_1 = 0;
		int op_2 = 0;
		int op_3 = 0;
		int op_4 = 0;

		// To support sign.
		// 1. The first character must not be operator.
		// e.g.
		// "+8" is 8.
		// "-8" is negative of 8.
		// "*8" is error (unable to parse "*8").
		// "/8" is error.
		//
		// 2. There is no double operator.
		// e.g.
		// "1++2" is 1 + positive of 2.
		// "1+-2" is 1 + negative of 2.
		// "1+*8" is error (unable to parse "*8").
		// "1+/8" is error.
		for (int i = 1; i < s.length(); i++) {
			final char c = s.charAt(i);

			if (c == '+') {
				op_1 = i;
				i++;
			} else if (c == '-') {
				op_2 = i;
				i++;
			} else if (c == '*') {
				op_3 = i;
				i++;
			} else if (c == '/') {
				op_4 = i;
				i++;
			}
		}

		if (op_1 != 0) {
			final BigDecimal l = calcO(s.substring(0, op_1));
			final BigDecimal r = calcO(s.substring(op_1 + 1));
			return (l.add(r, mc));
		} else if (op_2 != 0) {
			final BigDecimal l = calcO(s.substring(0, op_2));
			final BigDecimal r = calcO(s.substring(op_2 + 1));
			return (l.subtract(r, mc));
		} else if (op_3 != 0) {
			final BigDecimal l = calcO(s.substring(0, op_3));
			final BigDecimal r = calcO(s.substring(op_3 + 1));
			return (l.multiply(r, mc));
		} else if (op_4 != 0) {
			final BigDecimal l = calcO(s.substring(0, op_4));
			final BigDecimal r = calcO(s.substring(op_4 + 1));
			return (l.divide(r, mc));
		} else {
			return (new BigDecimal(s.toString()));
		}
	}

	public static void main(final String[] args) {
		if ((args != null) && (args.length == 2)) {
			final Calc o = new Calc(Integer.valueOf(args[0]));
			System.out.println(o.calcP(new StringBuffer(args[1])));
		} else {
			System.out.println("Usage: Calc [scale] [formula]");
			System.out.println("e.g. Calc 15 \"1 + (2 + 3) / 4 * 5\"");
		}
	}

}