
public class samsam extends Rule{
    private final int CHECK_SAMSAM = 2;
    private int stoneSum = 0;
    private int firstSum =0;
    private int secondSum = 0;

    private int flag;
    private static samsam instance = null;
    private samsam() {
        super();
    }

    public static samsam getInstance() {
        if(instance == null) instance = new samsam();
        return instance;
    }
    @Override
    public int sumCheck() {
        return CHECK_SAMSAM;
    }

    @Override //3체크 이후 열린 4인지 체크해야함
    public int openCheck(int FSS, int FSB, int SSS, int SSB, int num) {
        stoneSum = FSS + SSS;
        firstSum = FSS + FSB;
        secondSum = SSS + SSB;

        flag = 0;
        
        if(stoneSum != CHECK_SAMSAM)
            return 0;

        switch (num) {
            case 0:
                //가장자리에 돌을 뒀는지 확인
                if(y - firstSum == 0 || y + secondSum == 14)
                    return 0;

                //상대돌이 막고있는 지 확인
                if(board[y - firstSum - 1][x] == other_player || board[y + secondSum + 1][x] == other_player)
                    return 0;
                
                //돌을 뒀을 때 닫힌 4가 되는지 확인
                if(y - firstSum - 2 <= -1)
                    flag += 1;
                
                else {
                    if(board[y - firstSum- 2][x] == other_player)
                        flag += 1;
                }

                if(y + secondSum + 2 >= 15)
                    flag += 1;

                else {
                    if(board[y + secondSum + 2][x] ==other_player)
                        flag +=1;
                }

                if(flag == 2)
                    return 0;

                return 1;

            case 1:
                if(y - firstSum == 0 || x - firstSum == 0 || y + secondSum == 14 || x + secondSum == 14)
                    return 0;

                if(board[y - firstSum - 1][x - firstSum - 1] == other_player || board[y + secondSum + 1][x + secondSum + 1] == other_player)
                    return 0;

                if(y - firstSum - 2 <= -1)
                    flag += 1;

                else {
                    if(x - firstSum - 2 <= -1)
                        flag += 1;

                    else {
                        if(board[y - firstSum - 2][x - firstSum - 2] == other_player)
                            flag += 1;
                    }
                }

                if(y + secondSum + 2 >= 15)
                    flag += 1;

                else {
                    if(x + secondSum + 2 >= 15)
                        flag += 1;

                    else {
                        if(board[y + secondSum + 2][x + secondSum + 2] == other_player)
                            flag += 1;
                    }
                }

                if(flag == 2)
                    return 0;

                return 1;

            case 2:
                if(x - firstSum == 0 || x + secondSum == 14)
                    return 0;

                if(board[y][x - firstSum - 1] == other_player || board[y][x + secondSum + 1] == other_player)
                    return 0;

                if(x - firstSum - 2 <= -1)
                    flag += 1;

                else {
                    if(board[y][x - firstSum - 2] == other_player)
                        flag += 1;
                }

                if(x + secondSum +2 >= 15)
                    flag += 1;

                else {
                    if(board[y][x + secondSum + 2] == other_player)
                        flag += 1;
                }

                if(flag == 2)
                    return 0;

                return 1;

            case 3:
                if(x - firstSum == 0 || y - secondSum == 0 || y + firstSum == 14 || x + secondSum == 14)
                    return 0;

                if(board[y + firstSum + 1][x - firstSum - 1] == other_player || board[y - secondSum -1][x + secondSum + 1] == other_player)
                    return 0;

                if(y + firstSum + 2 >= 15)
                    flag += 1;

                else {

                    if(x - firstSum - 2 <= -1)
                        flag += 1;

                    else {
                        if(board[y + firstSum + 2][x - firstSum -2] == other_player)
                            flag += 1;
                    }
                }

                if(y - secondSum - 2 <= -1)
                    flag += 1;

                else {

                    if(x + secondSum + 2 >= 15)
                        flag += 1;

                    else {
                        if(board[y - secondSum - 2][x + secondSum + 2] == other_player)
                            flag += 1;
                    }
                }

                if(flag == 2)
                    return 0;

                return 1;
        }

        //error
        return 0;
    }


}
