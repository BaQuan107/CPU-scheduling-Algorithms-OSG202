package CPUSchedulingAlgorithms;

// @author Ajinkya

public class Process {
    //AT arrival time 
    //BT burst time 
    //RT remaining time
    //WT waiting time 
    //TAT total turn around time
    //flag = false ; process not completed

    public int arrivalTime, burstTime, waitingTime, totalTurnAroundTime, remainingTime, Pr, ST, id, ReST1, ReST2, vruntime, startTime, execTime;
    public boolean flag;
    public RedBlackTree rbt;

    public Process() {
        arrivalTime = 0;
        totalTurnAroundTime = 0;
        Pr = 0;
        ST = 0;
        ReST1 = 0;
        ReST2 = 0;
        flag = false;
    }
    //Copy constructor

    public Process(Process cp) {

        this.arrivalTime = cp.arrivalTime;
        this.totalTurnAroundTime = cp.totalTurnAroundTime;
        this.burstTime = cp.burstTime;
        this.flag = cp.flag;
        this.Pr = cp.Pr;
        this.ReST1 = cp.ReST1;
        this.waitingTime = cp.waitingTime;
        this.ReST2 = cp.ReST2;
        this.remainingTime = cp.remainingTime;
        this.ST = cp.ST;
    }

    public Process(int newId, int newArrivalTime, int newExecTime) {

        ST = 0;
        totalTurnAroundTime = 0;

        id = newId;
        execTime = newExecTime;
        arrivalTime = newArrivalTime;
        //WT=AT;
        vruntime = arrivalTime;
    }

    public Process(RedBlackTree rbtree, int newId, int newArrivalTime, int newExecTime) {

        ST = 0;

        id = newId;
        execTime = newExecTime;
        arrivalTime = newArrivalTime;
        //WT=AT;
        vruntime = 0;

        rbt = rbtree;
        rbt.insert(this);
    }

}
