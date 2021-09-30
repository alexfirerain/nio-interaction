public class Computer {
    public static final String NAME = "Great UnderSky Computing Interface";


    public static long fibonacci(int member) throws IllegalArgumentException {
        if (member < 1) throw new IllegalArgumentException("Non-natural computing not implemented");
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


}
