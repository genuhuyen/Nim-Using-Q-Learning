/* Huyen Tran
* Nim Game Simulation
*/
import java.util.*;
import java.util.Random;
import java.lang.*; //math fx.
import java.util.HashMap; //map
import java.util.ArrayList;

public class NimSim{
  int[] board = new int[3];
  int numGames;
  HashMap<StateAction, Double> qtable;

  public NimSim(int p0, int p1, int p2, int games){
    qtable = new HashMap<>();
    board[0] = p0;
    board[1] = p1;
    board[2] = p2;

    numGames = games;
  }

  public void runSim(){
    //initial board
    //initializeQTable("A012");
    String curPlayer  = "A";
    String otherPlayer = "B";
    int curGame = 1;
    int boardSum =  board[0] + board[1] + board[2];
    int initialBoardSum = boardSum;

    //random generator set up for action generation
    Random rand = new Random();
    int pileUB = 3; //pile number upper bound (0-2)
    int sticksUB = Math.max(board[0], board[1]); //number of sticks upperbound
    sticksUB = Math.max(sticksUB, board[2]);
    int pile, sticks;
    Double r; //reward

    //copy initial board to our curboard to be used during games
    int[] curBoard = new int[3];
    for(int i = 0; i < 3; i++){
      curBoard[i] = board[i];
    }


    //run n number of games
    while(curGame <= numGames){
        curPlayer = "A";
        //simulate each game - game isn't over until there's 0 sticks left
        while(boardSum > 0){
          //state before making any action
          String state = curPlayer + curBoard[0] + curBoard[1] + curBoard[2];

          //generate a random action
          pile = rand.nextInt(pileUB);
          sticks = rand.nextInt(sticksUB) + 1; //to make it 1...max sticks

          //generate valid action before we use it
          while(curBoard[pile] < sticks){
            pile = rand.nextInt(pileUB);
            sticks = rand.nextInt(sticksUB) + 1; //to make it 1...max sticks
          }

          String action = "" + pile + sticks;
          //store
          StateAction sa = new StateAction(state, action);
          if(!qtable.containsKey(sa)){
            qtable.put(sa, 0.0);
          }

          //make action and update board
          curBoard[pile] -= sticks;
          boardSum = curBoard[0] + curBoard[1] + curBoard[2];
          //determines next player
          if(curPlayer == "A"){
            otherPlayer = "B";
          }
          else{
            //cur player is B
            otherPlayer = "A";
          }
          //state of the next player
          String statePrime = otherPlayer + curBoard[0] + curBoard[1] + curBoard[2];
          String aPrime;

          //determines reward
          //game is over
          if(boardSum == 0){
            //determines winner
            if(curPlayer == "A"){
              r = -1000.0;
            }
            else{
              r = 1000.0;
            }
            //calculate Q[s, a]
            Double qSAprime = 0.0;
            Double qval = qtable.get(sa) + 1*(r + 0.9*(qSAprime) - qtable.get(sa));
            qtable.put(sa, qval);
            //System.out.printf("GAME:%s s:%s |  a:%s | r:%s | s':%s | q[s',a']: %s \n",curGame, state, action, r, statePrime, qSAprime);
            break;
          }
          else{
            r = 0.0;
          }
          Double qSAprime = getQSAprime(statePrime, curPlayer);
          //System.out.printf("GAME:%s s:%s |  a:%s | r:%s | s':%s | q[s',a']: %s \n",curGame, state, action, r, statePrime, qSAprime);
          //calculate Q[s, a]
          Double qval = qtable.get(sa) + 1*(r + 0.9*(qSAprime) - qtable.get(sa));
          qtable.put(sa, qval);

          //switch players
          if(curPlayer == "A"){
            curPlayer = "B";
          }
          else{
            curPlayer = "A";
          }
        }

        //restore to initial board for new game
        for(int i = 0; i < 3; i++){
          curBoard[i] = board[i];
        }
        //System.out.println();
        boardSum =  initialBoardSum;
        curGame++;
    }

  }

  public void printQTable(){
    for(StateAction sa : qtable.keySet()){
      System.out.printf("Q[%s, %s] = %s\n", sa.state(), sa.action(), qtable.get(sa));
    }
  }

  public HashMap<StateAction, Double> getQTable(){
    return qtable;
  }
  public Double getQSAprime(String statePrime, String curPlayer){
    //determines max/minQ[s', a']
    ArrayList<Double> qvalues = new ArrayList<Double>(); //stores q values of Q[s',a']
    ArrayList<String> actions = allPossActions(statePrime);
    for(int i = 0; i < actions.size(); i++){
      StateAction saPrime = new StateAction(statePrime, actions.get(i));
      //if state action not in table, just initialize it to 0
      if(!qtable.containsKey(saPrime)){
        qtable.put(saPrime, 0.0);
        qvalues.add(0.0);
      }
      else{
        qvalues.add(qtable.get(saPrime));
      }
    }

    Double qSAprime; //Q[s', a'] min/max based on cur player
    if(curPlayer == "A"){
      Double min = qvalues.get(0);
      for(int i = 0; i < qvalues.size(); i++){
        if(qvalues.get(i) < min){
          min = qvalues.get(i);
        }
      }
      //assign
      qSAprime = min;
    }
    else{
      curPlayer = "B";
      Double max = qvalues.get(0);
      for(int i = 0; i < qvalues.size(); i++){
        if(qvalues.get(i) > max){
          max = qvalues.get(i);
        }
      }
      //assign
      qSAprime = max;
    }
    return qSAprime;
  }

  //takes a state and generate a list of all possible actions
  public ArrayList<String> allPossActions(String state){
    ArrayList<String> allActions = new ArrayList<String>();
    int[] curState = {Integer.parseInt(String.valueOf(state.charAt(1))),
                      Integer.parseInt(String.valueOf(state.charAt(2))),
                      Integer.parseInt(String.valueOf(state.charAt(3)))};

    //loop through all piles and generate all possible action
    for(int p = 0; p < 3; p++){

      //for each pile, add all poss actions (count...1) actions
      for(int c = curState[p]; c > 0; c--){
        String action = "" + p + c;
        allActions.add(action);
      }
    }

    return allActions;
  }
}
