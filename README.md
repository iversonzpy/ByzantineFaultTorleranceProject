## BFT PROJECT
https://github.com/iversonzpy/ByzantineFaultTorleranceProject


In this project, we implemented a distributed Key-Value Database System.  
It includes three layers, application layer,transaction layer and replication layer.

##### System Architecture

![ImageOfStruture](https://raw.githubusercontent.com/iversonzpy/ByzantineFaultTorleranceProject/master/images/Structure.png)


##### YCSB Test Usage

###### Start Replicas

To run YCSB (TEST_TYPE = 1), run below command
```bash
java -cp bin/:lib/* bftproject.ycsbapp.occ.SerializeDatabase $REPLICA_INDEX $TEST_TYPE

```
or run scripts
```bash
./runscripts/startReplicaYCSB.sh 0 1
./runscripts/startReplicaYCSB.sh 1 1
./runscripts/startReplicaYCSB.sh 2 1
./runscripts/startReplicaYCSB.sh 3 1
```

##### Start YCSB Client

./runscripts/ycsbClient.sh


##### Online Shopping User Application Usage / Operation Interactive OCC Test Usage

###### Start Replicas

set TEST_TYPE = 2, run below command
```bash
java -cp bin/:lib/* bftproject.ycsbapp.occ.SerializeDatabase $REPLICA_INDEX $TEST_TYPE

```
or run scripts
```bash
./runscripts/startReplicaYCSB.sh 0 2
./runscripts/startReplicaYCSB.sh 1 2
./runscripts/startReplicaYCSB.sh 2 2
./runscripts/startReplicaYCSB.sh 3 2
```
###### Start Online Shopping User Application Client 
Set Client_ID = 0,1,2,...
```bash
./runscripts/userclient_start.sh 0
./runscripts/userclient_start.sh 1
```
or 
###### Start Operation Interactive OCC Test Client
Set Client_ID = 0,1,2,...
```bash
./runscripts/testclient.sh 0
./runscripts/testclient.sh 1
```

References: 

[1] B. Cooper et al, “Benchmarking Cloud Serving Systems with YCSB”, ACM Symposium on Cloud Computing (SoCC), Indianapolis, Indiana, June 2010.

[2] Kung, Hsiang-Tsung, and John T. Robinson. "On optimistic methods for concurrency control." ACM Transactions on Database Systems (TODS) 6.2 (1981): 213-226.

[3] A. N. Bessani, J. Sousa, and E. A. P. Alchieri. "State machine replication for the masses with BFT-SMART". In International Conference on Dependable Systems and Networks (DSN), pages 355–362, 2014.

[4] Optimal Concurrency Control, http://bft-smart.github.io/library/.

[5] BFT-Smart: https://github.com/mwhittaker/occ

