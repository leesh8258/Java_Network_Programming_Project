public class jangmok extends Rule{
    private final int CHECK_JANGMOK = 1;
    private int stoneSum = 0;
    private static jangmok instance = null;
    private jangmok() {
        super();
    }

    public static jangmok getInstance() {
        if(instance == null) instance = new jangmok();
        return instance;
    }

    @Override
    public int sumCheck() {
        return CHECK_JANGMOK;
    }

    @Override
    public int openCheck(int FSS, int FSB, int SSS, int SSB, int num) {
        stoneSum = FSS + SSS;
        if(stoneSum >= 5 && FSS != 0 && SSS != 0)
            return 1;
        else
            return 0;
    }
}
