/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CPUSchedulingAlgorithms;

import java.util.*;
import java.lang.Math;

//@author Ajinkya

public class Simulator {

    /**
     * @param args the command line arguments
     */
    static void printStart(Process[] pr) {
        System.out.println("=========================================================");
        System.out.println("Process\t" + "A.T\t" + "B.T\t" + "Priority");
        System.out.println("=========================================================");
        for (int i = 0; i < pr.length; i++) {
            System.out.println(i + "\t" + pr[i].arrivalTime + "\t" + pr[i].burstTime + "\t" + pr[i].Pr);
        }
    }

    static void update(Process p, Process[] pr) {

        int id = p.id;
        pr[id].waitingTime = p.waitingTime;
        pr[id].execTime = p.execTime;
        pr[id].ST += p.ST;
        pr[id].vruntime = p.vruntime;

    }

    static void printStats(Process[] pr) {
        System.out.println("=========================================================");
        System.out.println("Process\t" + "A.T\t" + "B.T\t" + "Pr\t" + "W.T\t" + "T.A.T");
        System.out.println("=========================================================");
        for (int i = 0; i < pr.length; i++) {
            pr[i].totalTurnAroundTime = pr[i].waitingTime + pr[i].burstTime;
            System.out.println(i + "\t" + pr[i].arrivalTime + "\t" + pr[i].burstTime + "\t" + pr[i].Pr + "\t" + pr[i].waitingTime + "\t" + pr[i].totalTurnAroundTime);
        }
        System.out.println("=========================================================");
        System.out.println("Stats\t" + "W.T\t" + "T.A.T\t");
        double wmean = 0, tmean = 0, wstd = 0, tstd = 0;
        for (int i = 0; i < pr.length; i++) {
            wmean += pr[i].waitingTime;
            tmean += pr[i].totalTurnAroundTime;
        }
        wmean /= pr.length;
        tmean /= pr.length;

        for (int i = 0; i < pr.length; i++) {
            double var = (wmean - pr[i].waitingTime);
            double var1 = (tmean - pr[i].totalTurnAroundTime);
            wstd += var * var;
            tstd += var1 * var1;
        }
        wstd = Math.sqrt(wstd);
        wstd = Math.round(wstd * 100.0) / 100.0;
        tstd = Math.sqrt(tstd);
        tstd = Math.round(tstd * 100.0) / 100.0;
        System.out.println("Mean\t" + wmean + "\t" + tmean);
        System.out.println("Std\t" + wstd + "\t" + tstd);
        System.out.println("===========================================================");
    }

    static void roundRobin(Process[] process) {
        int timeStop = 0, j = 0, k;
        int numProcess = process.length;
        while (j < numProcess) {
            k = 0;
            int quanta = 3;
            for (int i = 0; i < numProcess; i++) {
                if (process[i].flag || process[i].arrivalTime > timeStop) {
                    continue;
                }
                else {
                    if (process[i].remainingTime > quanta) {

                        process[i].ST += quanta;
                        process[i].remainingTime -= quanta;
                        timeStop += quanta;
                        k++;

                    }
                    else {
                        process[i].waitingTime += timeStop - process[i].ST;
                        timeStop += process[i].remainingTime;
                        process[i].ST += process[i].remainingTime;
                        process[i].remainingTime = 0;
                        process[i].flag = true;
                        j++;
                        k++;

                    }
                }
            }
            if (k == 0) {//if no process turns up then increase timeStop
                timeStop += 1;
            }

        }
        System.out.println("Round Robin : Total time taken=" + timeStop);
        printStats(process);
    }

    static void fifo(Process[] process) {
        int timeStop = process[0].arrivalTime, j = 0, k;
        int numProcess = process.length;
        for (int i = 0; i < numProcess; i++) {
            while (process[i].arrivalTime > timeStop) {
                timeStop++;
            }
            process[i].ST += process[i].burstTime;
            timeStop += process[i].burstTime;
            //System.out.println(process[i].AT);
            process[i].waitingTime += timeStop - process[i].ST;

            //process[i].id=i;
            process[i].flag = true;
        }
        System.out.println("FIFO : Total time taken=" + timeStop);
        printStats(process);

    }

    static void multilevelQueue(Process[] process) {
        //3 level queue
        //1. Round Robin with quanta=3s ; priority level(0,1)
        //2. Round Robin with quanta=5s ; priority level(2,3)
        //3. FIFO ;priority leve(4):lowest priority
        //Now we'll create 3 
        // ArrayList<Process> p1=new ArrayList<Process>();//Creating arraylist 
        ArrayList<Process> r1 = new ArrayList<Process>();//Ready queue for corresponding level
        // ArrayList<Process> p2=new ArrayList<Process>();
        ArrayList<Process> r2 = new ArrayList<Process>();
        // ArrayList<Process> p3=new ArrayList<Process>();
        ArrayList<Process> r3 = new ArrayList<Process>();
        /*for(int i=0;i<numProcess;i++){
                    if(process[i].Pr<=1)
                        p1.add(process[i]);
                    else if(process[i].Pr==4)
                        p3.add(process[i]);
                    else p2.add(process[i]);        
                }*/
        int numProcess = process.length;
        int timeStop = 0, checkPoint = 0;
        int n = 0;//to  record for no. of executed process
        while (true) {
            //System.out.println("main loop");
            int i = 0;//cursor for ready queue1
            int j = 0;//for q2
            int k = 0;//for q3
            for (int ii = 0; ii < numProcess; ii++) {
                if (process[ii].arrivalTime == timeStop) {
                    if (process[ii].Pr <= 1) {
                        r1.add(process[ii]);
                    }
                    else if (process[ii].Pr == 4) {
                        r3.add(process[ii]);
                    }
                    else {
                        r2.add(process[ii]);
                    }

                }
            }
            checkPoint = timeStop;
            //first process the first level with RR 
            if (!r1.isEmpty()) {
                //System.out.println("processing 1st queue");
                int quanta = 3;
                i = i % r1.size();
                while (i < r1.size()) {
                    Process pr = r1.get(i);
                    if (pr.remainingTime > quanta) {
                        pr.ST += quanta;
                        pr.remainingTime -= quanta;
                        timeStop += quanta;
                        i++;

                    }
                    else {

                        pr.waitingTime += timeStop - pr.ST;
                        pr.ST += pr.remainingTime;
                        timeStop += pr.remainingTime;
                        pr.remainingTime = 0;
                        pr.flag = true;
                        n++;
                        r1.remove(pr);

                    }
                }
                //Add all the process which came b/w previous checkpoint to this timeStop
                for (int jj = 0; jj < numProcess; jj++) {
                    if (process[jj].arrivalTime < timeStop && process[jj].arrivalTime > checkPoint) {
                        if (process[jj].Pr <= 1) {
                            r1.add(process[jj]);
                        }
                        else if (process[jj].Pr == 4) {
                            r3.add(process[jj]);
                        }
                        else {
                            r2.add(process[jj]);
                        }

                    }
                }
                checkPoint = timeStop;
            }
            else {//otherwise 
                if (!r2.isEmpty()) {//process the 2nd level for next one timeStop
                    //System.out.println("processing 2nd queue");
                    int quanta = 5;
                    //j=j%r2.size();

                    Process pr = r2.get(j);
                    if (pr.ST % quanta == quanta - 1 && pr.remainingTime != 1) {//Checking if this process is going to complete its quanta
                        pr.ST++;
                        pr.remainingTime--;
                        timeStop++;
                        j++;
                    }
                    else if (pr.remainingTime == 1 || (pr.remainingTime == 1 && pr.ST % quanta == quanta - 1)) {//checking if this is going to be completed
                        pr.waitingTime += timeStop - pr.ST;
                        pr.ST += pr.remainingTime;
                        timeStop++;
                        pr.remainingTime = 0;
                        pr.flag = true;
                        n++;
                        r2.remove(pr);

                    }
                    else {//otherwise this process should be continued on
                        pr.ST++;
                        pr.remainingTime--;
                        timeStop++;

                    }

                }
                else if (!r3.isEmpty()) {//process the 3rd level for next time stop by FIFO
                    //System.out.println("processing 3rd queue");
                    k = k % r3.size();
                    Process pr = r3.get(k);
                    if (pr.remainingTime == 1) {//process going to be completed
                        pr.waitingTime += timeStop - pr.ST;
                        pr.ST += pr.remainingTime;
                        timeStop++;
                        pr.remainingTime = 0;
                        pr.flag = true;
                        n++;
                        r3.remove(pr);
                    }
                    else {//otherwise this process should be continued on
                        pr.ST++;
                        pr.remainingTime--;
                        timeStop++;
                    }
                }
                else if (n == numProcess) {
                    System.out.println("Multilevel Queue : Total time taken=" + timeStop);
                    printStats(process);
                    break;
                }
                else {

                    timeStop++;
                }
            }
        }
    }

    static void multiLevelFeedback(Process[] process) {
        //3 level queue with feedback
        //1st level: Round Robin with quanta=3
        //2nd level: Round Robin with quanta=5
        //3rd level: FIFO
        //Create 3 queue
        ArrayList<Process> r1 = new ArrayList<Process>();
        ArrayList<Process> r2 = new ArrayList<Process>();
        ArrayList<Process> r3 = new ArrayList<Process>();
        int numProcess = process.length;
        int timeStop = 0;
        int l = 0;//for recording no. of executedd process
        int i = 0;//cursor for r1
        int j = 0;//cursor for r2
        int k = 0;//cursor for r3
        while (true) {

            int quanta1 = 3;
            int quanta2 = 5;
            for (int ll = 0; ll < numProcess; ll++) {
                if (process[ll].arrivalTime == timeStop) {
                    r1.add(process[ll]);//Any process first come to r1
                }
            }
            //We are not using directly Burst time and Remaining Time
            //to fulfill the condition we don't know the process burst 
            //time before it get completed.
            //Now process r1 with Round Robin with time quanta=3
            if (!r1.isEmpty()) {
                Process pr = r1.get(i);
                pr.ST++;
                timeStop++;
                if (pr.burstTime - pr.ST == 0) {//that is process completed
                    r1.remove(pr);
                    pr.flag = true;
                    l++;
                    pr.waitingTime += timeStop - pr.ST;

                }
                else if (pr.ST == quanta1) {//used upto time quanta limit 
                    r1.remove(pr);
                    r2.add(pr);

                }
                else if (pr.ST < quanta1) {//can use for time quanta
                    //do nothing
                    //repeat this process in next timeStop
                }
            }
            else {//Otherwise
                if (!r2.isEmpty()) {//Process second queue with Round Robin 
                    Process pr = r2.get(j);
                    pr.ST++;
                    timeStop++;
                    if (pr.burstTime - pr.ST == 0) {//that is process completed
                        r2.remove(pr);
                        pr.flag = true;
                        l++;
                        pr.waitingTime += timeStop - pr.ST;

                    }
                    else if (pr.ST == quanta2 + quanta1) {//used upto time quanta limit(added due to r1) 
                        r2.remove(pr);
                        r3.add(pr);

                    }
                    else if (pr.ST < quanta2) {//can use for time quanta
                        //do nothing
                        //repeat this process in next timeStop if the queue gets permit
                    }
                }
                else if (!r3.isEmpty()) {//Otherwise process last queue with FIFO
                    Process pr = r3.get(k);
                    pr.ST++;
                    timeStop++;
                    if (pr.burstTime - pr.ST == 0) {//that is process completed
                        r3.remove(pr);
                        //k++; //give chance to next process in the queue
                        pr.flag = true;
                        l++;
                        pr.waitingTime += timeStop - pr.ST;
                    }
                    else {//Process is not yet completed
                        //do nothing 
                        //repeat this process in next timeStop if the queue gets permit
                    }
                }
                else if (l == numProcess) {
                    System.out.println("Multilevel Feedback Queue : Total time taken=" + timeStop);
                    printStats(process);
                    break;
                }
                else {
                    timeStop++;
                }
            }
        }
    }

    static void multilevelFeedback(Process[] process) {
        //3 level queue with feedback with Ageing
        //1st level: Round Robin with quanta=3
        //2nd level: Round Robin with quanta=5
        //3rd level: FIFO
        //Create 3 queue

        ArrayList<Process> r1 = new ArrayList<Process>();
        ArrayList<Process> r2 = new ArrayList<Process>();
        ArrayList<Process> r3 = new ArrayList<Process>();
        int numProcess = process.length;
        int timeStop = 0;
        int n = 0;//record for no. of executed processes
        int i = 0;//cursor for r1
        int j = 0;//cursor for r2
        int k = 0;//cursor for r3
        while (true) {
            //System.out.println("loop0");

            int quanta1 = 3;
            int quanta2 = 5;
            //Introducing ageing facility
            int wLimit3 = 3 * numProcess;
            int wLimit2 = 4 * numProcess;
            for (int l = 0; l < numProcess; l++) {
                if (process[l].arrivalTime == timeStop) {
                    r1.add(process[l]);//Any process first come to r1
                }
            }
            //Each time we'll assume that processes will be waiting
            //We'll also check for wait limit for every process
            for (int l = 0; l < numProcess; l++) {
                //System.out.println("loop1");
                //Waiting limit for queue 2 is kept more since those crossed wait limit in queue 3
                //would have come to queue 2 and again they'll wait more.
                //We are sure that the process who crossed the wait limit of wLimit2 must exist in 
                //queue 2 if it doesn't error may arises
                if (process[l].waitingTime >= wLimit2 && !process[l].flag) {//wait limit
                    if (r2.remove(process[l])) {
                        r1.add(process[l]);//upgrade to queue 2
                        //System.out.println("process "+l+" added to r1 while removed from r2");
                        process[l].waitingTime = 0;
                    }
                }
                else if (process[l].waitingTime >= wLimit3 && !process[l].flag) {
                    if (r3.remove(process[l])) {
                        r2.add(process[l]);
                        //System.out.println("process "+l+" added to r2 while removed from r3");
                        process[l].waitingTime = 0;
                    }

                }
                if (!process[l].flag) {
                    process[l].waitingTime++;
                }
            }
            //We are not using directly Burst time and Remaining Time
            //to fulfill the condition we don't know the process burst 
            //time before it get completed.
            //Now process r1 with Round Robin with time quanta=3
            if (!r1.isEmpty()) {
                //System.out.println("r1loop1");
                Process pr = r1.get(i);

                pr.ST++;

                pr.waitingTime--;//Cancelling added wait time
                timeStop++;
                //System.out.println(timeStop+" "+pr.BT+" "+pr.ST+" "+pr.WT);
                if (pr.waitingTime < timeStop - pr.ST - pr.arrivalTime) {
                    pr.ReST1++;//this equation tells the validity of correct wait time
                }
                if (pr.burstTime - pr.ST == 0) {//that is process completed
                    r1.remove(pr);
                    pr.flag = true;
                    n++;
                    pr.waitingTime = timeStop - pr.ST - pr.arrivalTime;
                    //System.out.println("removedr1");

                }
                else if (pr.ST == quanta1 || pr.ReST1 == quanta1) {//used upto time quanta limit 
                    r1.remove(pr);
                    r2.add(pr);
                    pr.ReST1 = 0;//Reset it so that it can come again.

                }
                else if (pr.ST < quanta1 || pr.ReST1 < quanta1) {//can use for time quanta
                    //do nothing
                    //repeat this process in next timeStop
                }
            }
            else {//Otherwise
                if (!r2.isEmpty()) {//Process second queue with Round Robin 
                    //System.out.println("r2loop1");

                    Process pr = r2.get(j);

                    pr.ST++;

                    pr.waitingTime--;//Cancelling added wait time
                    timeStop++;
                    //System.out.println(timeStop+" "+pr.BT+" "+pr.ST+" "+pr.WT);
                    if (pr.waitingTime < timeStop - pr.ST - pr.arrivalTime) {
                        pr.ReST2++;//System.out.println("somd");}
                    }
                    if (pr.burstTime - pr.ST == 0) {//that is process completed
                        r2.remove(pr);
                        pr.flag = true;
                        n++;
                        pr.waitingTime = timeStop - pr.ST - pr.arrivalTime;
                        //System.out.println("removedr2");

                    }
                    else if (pr.ST == quanta2 + quanta1 || pr.ReST2 == quanta2) {//used upto time quanta limit(added due to r1) 
                        r2.remove(pr);
                        r3.add(pr);
                        pr.ReST2 = 0;//Reset it so that it can come again.

                    }
                    else if (pr.ST < quanta2 || pr.ReST2 < quanta2) {//can use for time quanta
                        //do nothing
                        //repeat this process in next timeStop if the queue gets permit
                    }
                }
                else if (!r3.isEmpty()) {//Otherwise process last queue with FIFO
                    //System.out.println("r3loop1");
                    Process pr = r3.get(k);
                    pr.ST++;
                    timeStop++;
                    pr.waitingTime--;//Cancelling added wait time
                    if (pr.burstTime - pr.ST == 0) {//that is process completed
                        r3.remove(pr);
                        //k++; //give chance to next process in the queue
                        pr.flag = true;
                        n++;
                        pr.waitingTime = timeStop - pr.ST - pr.arrivalTime;
                    }
                    else {//Process is not yet completed
                        //do nothing 
                        //repeat this process in next timeStop if the queue gets permit
                    }
                }
                else if (n == numProcess) {
                    System.out.println("Multilevel Feedback Queue With Ageing : Total time taken=" + timeStop);
                    printStats(process);
                    break;
                }
                else {
                    timeStop++;
                }
            }
        }
    }

    static void srtf(Process[] process) {
        int i;
        int current_time = 0;
        int total_burst_time = 0;
        int shortest_remain_time = 0;
        int k = 0;
        int numProcess = process.length;

        int[] remain_burst_time = new int[numProcess];
        int[] count = new int[numProcess];//would help in determining response time
        for (i = 0; i < numProcess; i++) {
            process[i].waitingTime = 0;
            process[i].totalTurnAroundTime = 0;
        }
        i = 0;
        while (i < numProcess) {
            count[i] = 0;
            remain_burst_time[i] = process[i].burstTime;
            total_burst_time = total_burst_time + process[i].burstTime;
            i++;
        }
        //Repeat until the current time is the total execution time
        for (current_time = 0; current_time < total_burst_time;) {
            //Initialize minimum operation index to INT_MAX
            shortest_remain_time = 1000;

            //Less than the arrival time of the last incoming process
            if (current_time <= process[numProcess - 1].arrivalTime) {
                i = 0;
                while (i < numProcess) {
                    if ((process[i].flag == false) && (shortest_remain_time > remain_burst_time[i])
                            && (process[i].arrivalTime <= current_time)) {
                        shortest_remain_time = remain_burst_time[i];
                        k = i;
                    }
                    i++;
                }
            }
            else {
                i = 0;
                while (i < numProcess) {
                    if ((shortest_remain_time > remain_burst_time[i]) && (process[i].flag == false)) {
                        shortest_remain_time = remain_burst_time[i];
                        k = i;
                    }
                    i++;
                }
            }

            if (count[k] == 0) {
                count[k] += 1;
                process[k].remainingTime = current_time;//rt is responsetime here
            }

            remain_burst_time[k] -= 1;
            current_time += 1;

            if (remain_burst_time[k] == 0) {
                process[k].flag = true;
                process[k].remainingTime = current_time;
                process[k].waitingTime = current_time - process[k].burstTime;
                process[k].waitingTime -= process[k].arrivalTime;
            }
        }
        i = 0;
        while (i < numProcess) {
            process[i].totalTurnAroundTime = process[i].remainingTime - process[i].arrivalTime;
            i++;
        }
        System.out.println("Shortest Remaining time First(Preemptive)");
        printStats(process);
    }

    static void ppri(Process[] process) {
        int i, k = 0;
        int current_time = 0;
        int total_burst_time = 0;
        int priority;

        int numProcess = process.length;

        int[] remain_burst_time = new int[numProcess];
        int[] count = new int[numProcess];//would help in determining response time
        for (i = 0; i < numProcess; i++) {
            process[i].waitingTime = 0;
            process[i].totalTurnAroundTime = 0;
        }
        for (i = 0; i < numProcess; i++) {
            count[i] = 0;
            total_burst_time = total_burst_time + process[i].burstTime;
            remain_burst_time[i] = process[i].burstTime;
        }
        //Repeat until the current time is the total execution time

        for (current_time = 0; current_time < total_burst_time;) {
            //Initialize minimum operation index to INT_MAX
            priority = 1000;

            //Less than the arrival time of the last incoming process
            if (current_time <= process[numProcess - 1].arrivalTime) {
                i = 0;
                while (i < numProcess) {
                    if ((process[i].flag == false) && (priority > process[i].Pr) && (process[i].arrivalTime <= current_time)) {
                        priority = process[i].Pr;
                        k = i;
                        i++;
                    }
                    else i++;
                }
            } else {
                i = 0;
                while (i < numProcess) {
                    if ((priority > process[i].Pr) && (process[i].flag == false)) {
                        priority = process[i].Pr;
                        k = i;
                        i++;
                    }
                    else i++;
                }
            }

            if (count[k] == 0) {
                count[k] += 1;
                process[k].remainingTime = current_time;//rt is responsetime here
            }

            remain_burst_time[k] -= 1;
            current_time += 1;

            if (remain_burst_time[k] == 0) {
                process[k].flag = true;
                process[k].remainingTime = current_time;
                process[k].waitingTime = process[k].remainingTime - process[k].burstTime;
                process[k].waitingTime -= process[k].arrivalTime;
            }
        }
        i = 0;
        while (i < numProcess) {
            process[i].totalTurnAroundTime = process[i].remainingTime - process[i].arrivalTime;
            //process[i].WT=process[i].TAT-process[i].AT-process[i].BT;
            i++;
        }
        System.out.println(current_time);
        System.out.println("Preemptive priority scheduling");
        printStats(process);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static void pri(Process[] process) {
        int i, j, k = 0;
        int curr_time = 0;
        int min = 0, temp = 0;
        //process[0].flag=true;
        //process[0].RT=process[0].BT;//return time
        //process[0].TAT=process[0].BT-process[0].AT;

        int numProcess = process.length;
        //curr_time=process[0].BT;
        for (i = 0; i < numProcess; i++) {
            process[i].waitingTime = 0;
            process[i].totalTurnAroundTime = 0;
        }
        int executed = 0;
        while (executed < numProcess) {
            j = 0;
            while (j < numProcess) {
                if (process[j].flag == true) {
                    j++;
                    continue;
                } else {
                    min = j;
                    System.out.println();
                    System.out.print(j);
                    break;
                }

            }
            j = 0;
            while (j < numProcess) {
                if ((process[j].flag == false) && (process[j].Pr <= process[min].Pr)
                        && (process[j].arrivalTime <= curr_time)) {
                    min = j;
                    System.out.print(" "+j);
                    temp = 1;
                }
                j++;
            }
            if (temp == 1) {
                process[min].waitingTime = curr_time - process[min].arrivalTime;
                process[min].flag = true;
                executed++;
                curr_time = process[min].burstTime + curr_time;
                process[min].remainingTime = curr_time;
                process[min].totalTurnAroundTime = process[min].remainingTime - process[min].arrivalTime;
                temp = 0;
                System.out.println(curr_time);
            } else {
                curr_time++;
                temp = 0;
            }
        }
        for (i = 0; i < numProcess; i++) {
            process[i].remainingTime = process[i].totalTurnAroundTime + process[i].arrivalTime;
            //TWT += process[i].waiting_time;
            //	TTAT += process[i].turnaround_time;
        }
        System.out.println("Priority Scheduling");
        printStats(process);
    }

    ///////////////////////////////////////////////////////SJF///////////////////////////////////////////////////////////////////
    static void sjf(Process[] process) {
        int i, j, k = 0;
        int curr_time = 0;
        int min = 0, temp = 0;
        //process[0].flag=true;
        //process[0].RT=process[0].BT;//return time
        //process[0].TAT=process[0].BT-process[0].AT;

        int numProcess = process.length;
        //curr_time=process[0].BT;
        for (i = 0; i < numProcess; i++) {
            process[i].waitingTime = 0;
            process[i].totalTurnAroundTime = 0;
        }
        
        int executed = 0;
        while (executed < numProcess) {
            j = 0;
            temp = 0;
            // ??i t??m th???ng ch??a ???????c ???????c x??? l?? c?? ch??? s??? b?? nh???t 
            while (j < numProcess) {
                if (process[j].flag == true) {
                    j++;
                    continue;
                }
                else {
                    min = j;
                    break;
                }
            }
            
            j = 0;
            while (j < numProcess) {
                if ((process[j].flag == false) && (process[j].burstTime <= process[min].burstTime)
                        && (process[j].arrivalTime <= curr_time)) {
                    min = j;
                    temp = 1;
                }
                j++;
            }
            
            
            if (temp == 1) {
                process[min].waitingTime = curr_time - process[min].arrivalTime;

                process[min].flag = true;
                executed++;
                curr_time = curr_time + process[min].burstTime;
                process[min].remainingTime = curr_time;
                process[min].totalTurnAroundTime = process[min].remainingTime - process[min].arrivalTime;
            }
            else {
                curr_time++;
            }
        }
        
        System.out.println("Shortest Job First(Non-Preemptive)");
        printStats(process);
    }

    static void cfs(Process[] process) {
        RedBlackTree rbt;
        int node_count = 0;
        int numProcess = process.length;
        //rbt first node
        rbt = new RedBlackTree();
        ArrayList<Process> processes = new ArrayList<Process>();
        for (int j = 0; j < numProcess; j++) {
            int l = 0, m = 0, n = 0;
            l = j;
            m = process[j].arrivalTime;
            n = process[j].burstTime;
            processes.add(new Process(rbt, l, m, n));
            node_count++;

        }

        //System.out.println("checkpoint1");
        int quantumTime = 3;

        Process p;
        int timeStop = 0;

        int totalWaitTime = 0;
        int totalTurnAroundTime = 0;
//        int timeKeeper=0;
//       System.out.println("\n\nScheduling using red black tree data structure...\n");
//       System.out.println("\nScheduling Metric/Unfairness Measure--Time In Processor--");

        while (RedBlackTree.NodeCount > 1) {
            // long start = System.nanoTime();
            p = rbt.delete().process;

            //System.out.println("checkpoint2");
            if (p.execTime > quantumTime) {
                p.vruntime = p.vruntime + quantumTime;
                p.ST = p.ST + quantumTime;
                p.execTime = p.execTime - quantumTime;
                timeStop = timeStop + quantumTime;
                update(p, process);
                //p.WT=timeStop- p.AT- p.ST; 
                if (p.execTime > 0) {
                    rbt.insert(p);
                }
                //System.out.println("ifcheckpoint");

            }
            else {
                p.ST = p.ST + p.execTime;
                timeStop = timeStop + p.execTime;
                p.waitingTime = timeStop - p.ST - p.arrivalTime;

                //System.out.println(p.WT+" "+p.id);
                p.execTime = 0;
                totalWaitTime += p.waitingTime;
                update(p, process);
                // p.TAT=timeStop-p.AT;
                totalTurnAroundTime += p.totalTurnAroundTime;
                //System.out.println("checkpointelse");

            }
            //System.out.println("checkpoint1");

//	
        }
        //timeStop+=process[0].AT;

        System.out.println("CFS : Total time Used in CPU =" + timeStop);
        printStats(process);

    }

    public static void main(String[] args) {
        // TODO code application logic here
        //Generate Process Randomly
        //ATAT average turn around time
        //AWT average waiting time
        int numProcess;
        int arrival = 0, at, ATAT = 0, AWT = 0;
        Scanner sf = new Scanner(System.in);
        System.out.println("Enter the no. of Process");
        numProcess = sf.nextInt();
        Random rand = new Random();
        Process[] process = new Process[numProcess];
        //Create a queue for all the process
        Queue<Process> processQ = new LinkedList<>();
        for (int i = 0; i < numProcess; i++) {

            process[i] = new Process();

            process[i].burstTime = rand.nextInt(10) + 1;
            process[i].Pr = rand.nextInt(5);
            process[i].arrivalTime = arrival;
            arrival += rand.nextInt(2);

            process[i].waitingTime = -1 * process[i].arrivalTime;
            process[i].remainingTime = process[i].burstTime;
            processQ.add(process[i]);
        }

        for (int i = 0; i < numProcess; i++) {
            process[i].waitingTime = -1 * process[i].arrivalTime;
            process[i].remainingTime = process[i].burstTime;
        }

        printStart(process);

        while (true) {
            System.out.println("Enter the options:");
            System.out.println("1.FIFO  2.Round Robin  3.priority 4.preemptive priority 5.Sjf 6.srtf ");
            System.out.println("7.Multilevel Queue 8.multilevel Feedback   9.Multilevel Feedback with Ageing");
            System.out.println("10.CFS Scheduling");
            System.out.println("Press 0 to exit!!");
            System.out.print("Enter your option: ");
            int option = sf.nextInt();

            Process[] cprocess = new Process[numProcess];
            for (int i = 0; i < numProcess; i++) {
                cprocess[i] = new Process(process[i]);//cloning the process
            }
            switch (option) {
                case 1://FIFO
                    fifo(cprocess);
                    break;
                case 2://Round Robin
                    roundRobin(cprocess);
                    break;
                case 3://Priority Non-preemptive
                    pri(cprocess);
                    break;
                case 4://Priority Preemptive
                    ppri(cprocess);
                    break;
                case 5://SJF
                    sjf(cprocess);
                    break;
                case 6://SRTF
                    srtf(cprocess);
                    break;
                case 7://MultiLevel Queue
                    multilevelQueue(cprocess);
                    break;
                case 8://Multilevel Feedback Queue
                    multiLevelFeedback(cprocess);
                    break;
                case 9://Multilevel Feedback with Ageing
                    multilevelFeedback(cprocess);
                    break;
                case 10://CFS
                    cfs(cprocess);
                    break;
            }
            if (option == 0) {
                break;
            }
        }

    }

}
