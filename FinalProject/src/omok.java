public class omok extends Rule{

    private static omok instance = null;
    private omok() {
        super();
    }

    public static omok getInstance() {
        if(instance == null) instance = new omok();
        return instance;
    }
    @Override
    public int sumCheck() {
        return 1;
    }

    @Override
    public int openCheck(int FSS, int FSB, int SSS, int SSB, int num) {
        int stoneSum = FSS + SSS;
        if(stoneSum == 4)
            return 1;
        else
            return 0;
    }
}
