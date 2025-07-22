public class sasa extends Rule{
    private static sasa instance = null;

    private sasa() {
        super();
    }

    public static sasa getInstance() {
        if(instance == null) instance = new sasa();
        return instance;
    }
    @Override
    public int sumCheck() {
        return 2;
    }

    @Override
    public int openCheck(int FSS, int FSB, int SSS, int SSB, int num) {
        int stoneSum = FSS + SSS;

        if(stoneSum != 3)
            return 0;
        else
            return 1;
    }
}
