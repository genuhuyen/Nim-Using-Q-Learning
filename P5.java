/* Huyen Tran
* Nim Game
* 5/2/2022
* I have neither given nor received unauthorized aid on this program
*/
import java.util.Scanner;
import java.util.*;
import java.util.Random;
import java.lang.*; //math fx.
import java.util.HashMap; //map
import java.util.ArrayList;

public class P5{

  //driver code
  public static void main(String[] args){
    //get user input
    Scanner input = new Scanner(System.in);
    System.out.print("Number in pile 0? ");
    int p0count = input.nextInt();
    System.out.print("Number in pile 1? ");
    int p1count = input.nextInt();
    System.out.print("Number in pile 2? ");
    int p2count = input.nextInt();

    //store values in intial board
    int[] board = {p0count, p1count, p2count};

    //num games to simulate
    System.out.println();
    System.out.print("Number of games to simulate? ");
    int numGames = input.nextInt();
    System.out.println();
    System.out.printf("Initial board is %d-%d-%d, simulating %d games.\n", p0count, p1count, p2count, numGames);

    //simulate games
    NimSim sim = new NimSim(p0count, p1count, p2count, numGames);
    sim.runSim();
    sim.printQTable();

    //store qtable
    HashMap<StateAction, Double> qtable = sim.getQTable();

    System.out.println();
    int pAgain = 1;
    while(pAgain == 1){
      System.out.print("Who moves first, (1) User of (2) Computer? ");
      int playerA = input.nextInt();
      System.out.println();

      String user, comp;
      if(playerA == 1){
        user = "A";
        comp = "B";
      }
      else{
        comp = "A";
        user = "B";
      }

      String curPlayer = "A";

      //copy initial board to our curboard to be used during games
      int[] curBoard = new int[3];
      for(int i = 0; i < 3; i++){
        curBoard[i] = board[i];
      }
      int boardSum = curBoard[0] + curBoard[1] + curBoard[2];

      int pile, sticks;
      while(boardSum > 0){
        String state = curPlayer + curBoard[0] + curBoard[1] + curBoard[2];
        //computer makes optimal move from qtable
        //computer's turn
        if(curPlayer.equals(comp)){
          //find optimal action with the max q value
          ArrayList<String> actions = sim.allPossActions(state);

          //find max qvalues if computer is A, min if computer is B
          StateAction sa = new StateAction(state, actions.get(0));
          Double maxQ = 0.0;
          Double minQ = 0.0;
          if(comp.equals("A")){
            maxQ = qtable.get(sa); //best q-value
          }
          else{
            minQ = qtable.get(sa);
          }
          String bestA = actions.get(0); //best action
          for(int i = 0; i < actions.size(); i++){
            sa = new StateAction(state, actions.get(i));
            if(comp.equals("A")){
              if(qtable.get(sa) > maxQ){
                maxQ = qtable.get(sa);
                bestA = actions.get(i);
              }
            }
            else{
              if(qtable.get(sa) < minQ){
                minQ = qtable.get(sa);
                bestA = actions.get(i);
              }
            }
          }
          //make the best move
          pile = Integer.parseInt(String.valueOf(bestA.charAt(0)));
          sticks = Integer.parseInt(String.valueOf(bestA.charAt(1)));

          System.out.printf("Player %s (computer)'s turn; board is (%s, %s, %s).\n",
                              curPlayer, curBoard[0], curBoard[1], curBoard[2]);
          System.out.printf("Computer chooses pile %s and removes %s.\n\n", pile, sticks);
          //update board
          curBoard[pile] -= sticks;
          boardSum = curBoard[0] + curBoard[1] + curBoard[2];
        }

        //user's turn
        else{
          System.out.printf("Player %s (user)'s turn; board is (%s, %s, %s).\n",
                              curPlayer, curBoard[0], curBoard[1], curBoard[2]);
          System.out.print("What pile? ");
          pile = input.nextInt();
          //error checking
          while(pile > 2 || curBoard[pile] <= 0){
            System.out.print("What pile? ");
            pile = input.nextInt();
          }

          System.out.print("How many? ");
          sticks = input.nextInt();
          while(sticks > curBoard[pile]){
            System.out.print("How many? ");
            sticks = input.nextInt();
          }

          //make move
          curBoard[pile] -= sticks;
          boardSum = curBoard[0] + curBoard[1] + curBoard[2];
        }

        //game over check
        if(boardSum == 0){
          System.out.println();
          System.out.println("Game over.");
          if(curPlayer.equals(comp)){
            System.out.printf("Winner is %s (user)\n\n", user);
          }
          else{
            System.out.printf("Winner is %s (computer)\n\n", comp);
          }
          System.out.print("Play again? (1) Yes (2) No: ");
          pAgain = input.nextInt();
        }

        //switch users
        if(curPlayer == "A"){
          curPlayer = "B";
        }
        else{
          curPlayer = "A";
        }

      }
      //reset board
      for(int i = 0; i < 3; i++){
        curBoard[i] = board[i];
      }
    }
  }
}
