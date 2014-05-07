import java.math.BigInteger;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Qubo Song on 5/6/2014.
 */
public class Dlog {


    public static void main(String[] args) {

        BigInteger p = new BigInteger("134078079299425970995740249982058461274793658205923933" +
                                      "77723561443721764030073546976801874298166903427690031" +
                                      "858186486050853753882811946569946433649006084171");
        BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205" +
                                      "59997945906392949973658374667057217647146031292859482967" +
                                      "5428279466566527115212748467589894601965568");
        BigInteger h = new BigInteger("323947510405045044356526437872806578864909752095244" +
                                      "952783479245297198197614329255807385693795855318053" +
                                      "2878928001494706097394108577585732452307673444020333");
        Integer length = 20;
        Integer B = (int)Math.pow(2, length);
        BigInteger[] exp_g_invs = new BigInteger[length + 1];
        BigInteger[] exp_gB = new BigInteger[length + 1];
        Map<BigInteger, Integer> left = new HashMap<BigInteger, Integer>(B + 1);
        Integer x0, x1;
        BigInteger gTemp, leftTemp, rightTemp, result;

        Integer beacon = -1;

        System.out.printf("Starting to calculate Dlog_g(h) under modulo p...\n");
        System.out.printf("Building up hash table for middle attack...");

        exp_g_invs[0] = g.modInverse(p);
        for (int iLength = 1; iLength < length + 1; iLength++) {
            exp_g_invs[iLength] = exp_g_invs[iLength - 1].modPow(BigInteger.valueOf(2), p);
        }



        for (x1 = 0; x1 < B + 1; x1++) {

            String polynomial;

            if (x1 * 100 / B > beacon) {
                beacon = x1 * 100 / B;
                System.out.printf("%d%% completed...\n", beacon);
                System.out.flush();
            }

            polynomial = Integer.toBinaryString(x1);
            gTemp = BigInteger.ONE;

            for (int iLength = 0; iLength < polynomial.length(); iLength++) {
                if ('1' == polynomial.charAt(polynomial.length() - 1 - iLength)) {
                    gTemp = gTemp.multiply(exp_g_invs[iLength]).mod(p);
                }
            }

            leftTemp = h.multiply(gTemp).mod(p);

            left.put(leftTemp, x1);
        }

        System.out.printf("Searching for collision...\n");
        beacon = -1;
        exp_gB[0] = g.modPow(BigInteger.valueOf(B), p);
        for (int iLength = 1; iLength < length + 1; iLength++) {
            exp_gB[iLength] = exp_gB[iLength - 1].modPow(BigInteger.valueOf(2), p);
        }

        for (x0 = 0; x0 < B + 1; x0++) {

            String polynomial;

            if (x0 * 100 / B > beacon) {
                beacon = x0 * 100 / B;
                System.out.printf("%d%% range covered...\n", beacon);
                System.out.flush();
            }

            polynomial = Integer.toBinaryString(x0);
            rightTemp = BigInteger.ONE;

            for (int iLength = 0; iLength < polynomial.length(); iLength++) {
                if ('1' == polynomial.charAt(polynomial.length() - 1 - iLength)) {
                    rightTemp = rightTemp.multiply(exp_gB[iLength]).mod(p);
                }
            }
            if(left.containsKey(rightTemp)) {
                System.out.printf("Found...");
                x1 = left.get(rightTemp);
                break;
            }
        }

        result = new BigInteger(B.toString()).multiply(BigInteger.valueOf(x0)).add(BigInteger.valueOf(x1));
        System.out.printf("Dlog_g(h) is: " + result + "\n");

    }
}
