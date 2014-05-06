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

        Integer B = (int)Math.pow(2, 20);
        Map<BigInteger, Integer> left = new HashMap<BigInteger, Integer>(B + 1);
        Integer x0, x1;
        BigInteger result;

        Integer beacon = -1;

        System.out.printf("Starting to calcuate Dlog_g(h) under modulo p...\n");
        System.out.printf("Building up hash table for middle attack...\n");
        for (x1 = 0; x1 < B + 1; x1++) {
            if ((int)(x1 * 100 / B) > beacon) {
                beacon = (int)(x1 * 100 / B);
                System.out.printf("%d%% completed...\n", beacon);
                System.out.flush();
            }
            // power: 2^20, 20 bit.
            left.put(h.multiply(g.modPow(new BigInteger("-" + x1.toString()), p)).mod(p), x1);
        }

        System.out.printf("Searching for collision...\n");
        for (x0 = 0; x0 < B + 1; x0++) {
            BigInteger right = g.modPow(new BigInteger(B.toString()), p).modPow(new BigInteger(x0.toString()), p);
            if(left.containsKey(right)) {
                System.out.printf("Found...");
                x1 = left.get(right);
                break;
            }
        }

        result = new BigInteger(B.toString()).multiply(new BigInteger(x0.toString())).add(new BigInteger(x1.toString()));
        System.out.printf("Dlog_g(h) is: " + result);

    }
}
