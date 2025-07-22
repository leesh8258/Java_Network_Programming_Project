public abstract class Rule {
    protected int current_player;
    protected int other_player;
    protected int[][] board;
    protected int y, x;

    private final int[] dy = {-1, -1, 0, 1};
    private final int[] dx = {0, -1, -1, -1};
    private final int BLINK = 0;


    public boolean checkRule(int current_player, int[][]board, int y, int x) {
        this.current_player = current_player;
        this.other_player = 3 - current_player; //current_player == 1, other_player == 2
        this.board = board;
        this.y = y;
        this.x = x;

        int sum = 0;
        for(int i =0; i < dx.length; i++) {
            sum += eightDirectionCheck(dy[i], dx[i], i);
        }

        return sum >= sumCheck();
    }

    public boolean checkOmok(int current_player, int[][]board, int y, int x) {
        this.current_player = current_player;
        this.other_player = 3 - current_player;
        this.board = board;
        this.y = y;
        this.x = x;

        int sum = 0;
        for(int i =0; i < dx.length; i++) {
            sum += eightDirectionCheckNotBlink(dy[i], dx[i]);
        }

        return sum >= sumCheck();
    }

    public int eightDirectionCheckNotBlink(int y, int x) {
        int first;
        int second;

        first = findNotBlink(y, x);
        second = findNotBlink(-y, -x);

        return openCheck(first, 0, second, 0, 0);
    }

    private int findNotBlink(int varY, int varX) {
        int stone = 0;
        int dy, dx;
        dy = y + varY;
        dx = x + varX;

        while(dy >= 0 && dy < 15 && dx >= 0 && dx < 15) {
            if(board[dy][dx] == other_player || board[dy][dx] == BLINK) //다른 돌을 만나면 중지
                break;

            if(board[dy][dx] == current_player) { //자신 돌을 만나면
                stone += 1;
            }

            dy += varY;
            dx += varX;
        }
        return stone;
    }

    private int[] find(int varY, int varX, int _blink) {
        int[] ans = new int[2];
        int stone = 0;
        int blink = _blink;
        int dy, dx;
        boolean is_Check = true;
        dy = y + varY;
        dx = x + varX;

        while(dy >= 0 && dy < 15 && dx >= 0 && dx < 15) {

            if(board[dy][dx] == other_player) //다른 돌을 만나면 중지
                break;

            if(board[dy][dx] == current_player) { //자신 돌을 만나면
                is_Check = true; // 빈칸을 안만나면 다시 초기화
                stone += 1;
            }

            if(board[dy][dx] == BLINK) { //빈칸을 만나면
                if(is_Check)
                    is_Check = false;

                else {
                    blink += 1; //두번연속 빈칸을 만나면 blink 초기화
                    break;
                }

                if(blink == 1)
                    blink -= 1;

                else
                    break;
            }

            dy += varY;
            dx += varX;
        }

        ans[0] = stone;
        ans[1] = blink;
        return ans;

    }
    //8방향으로 조건 체크
    private int eightDirectionCheck(int varY, int varX, int num) {
        int[] temp;

        int firstSearchStone, secondSearchStone;
        int firstSearchBlink = 1, secondSearchBlink;

        temp = find(varY, varX, firstSearchBlink);
        firstSearchStone = temp[0];
        firstSearchBlink = temp[1];

        secondSearchBlink = firstSearchBlink;
        if(firstSearchBlink == 1)
            firstSearchBlink = 0;

        temp = find(-varY, -varX, secondSearchBlink);
        secondSearchStone = temp[0];
        secondSearchBlink = temp[1];


        return openCheck(firstSearchStone, firstSearchBlink, secondSearchStone, secondSearchBlink, num);
    }
    public abstract int sumCheck();
    public abstract int openCheck(int FSS, int FSB, int SSS, int SSB, int num);
}
