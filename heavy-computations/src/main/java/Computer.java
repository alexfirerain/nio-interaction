public class Computer {
    public static final String NAME = "Great UnderSky Computing Interface";


    public static long fibonacci(int member) throws IllegalArgumentException {
        if (member < 1)
            throw new IllegalArgumentException("Non-natural computing not implemented");
        if (member > 93)
            throw new IllegalArgumentException("Computing over 4-bytes result not implemented");

        int next = 1;
        long prev = 1;
        long cur = 0;
        while (next < member) {
            long nextPrev = cur;
            cur += prev;
            prev = nextPrev;
            next++;
        }
        return cur;
    }

    public static String nanoTimeFormatter(long duration) {
        if (duration < 10_000)
            return duration + " нс";
        if (duration > 1_000_000_000)
            return "%.3f с".formatted((double) duration / 1_000_000_000f);
        return "%.3f мс".formatted((double) duration / 1_000_000f);
    }

//    public static void main(String[] args) {
//        System.out.println(Long.MAX_VALUE);
//        for (int i = 1; i < 100; i++) {
//            System.out.println(i + "\t" + fibonacci(i));
//        }
//    }
}