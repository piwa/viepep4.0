# ViePEP 4.0

In order to get ViePEP running you need CPLEX, using the open source library LP_Solve does not work!

* register at IBM academic initiative
* download CPlex
* install it on your local system, you will be asked for the installation folder
* go to the installation folder
    * you need two files:
	    * libcplex1262.so (for MAC OSX: libcplex1262.jnilib)
	    * cplex.jar
    * copy these files to
		* lib/jar/cplex.jar
        * lib/natives/libcplex1262.so
        * at.ac.tuwien.infosys.main/src/main/resources/natives/libcplex1262.so (for MAC OSX: at.ac.tuwien.infosys.main/src/main/resources/natives/libcplex1262.jnilib)

* download the Java ILP library from [http://javailp.sourceforge.net](http://javailp.sourceforge.net)
    * copy it to lib/jar/javailp-1.2a.jar

* create a database on your server and update the configuration in: /src/main/resources/mysql-config
    * copy the sample file if not present

* update the cloud configuration in: /src/main/resources/cloud-config
    * there are no backend services deployed on default, hence, make sure to have them available, e.g., you can use
        * [https://github.com/bonomat/viepep-backend-services](https://github.com/bonomat/viepep-backend-services)
        * it is a configurable service which depending on the request parameter simulated the load, check the code for further details
    * update the settings in /src/main/resources/docker-config/swarm-node-cloud-config.yaml
        * insert your public key in the last line and change other settings aswell if required

* start ViePEP with
    * the optimization should run at least once! The output needs to look similar to:

```
			---------tau_t_0 : Tue Nov 03 13:33:14 CET 2015 ------------------------
			---------tau_t_0.time : 1446553994681 ------------------------
			Found incumbent of value 0.000000 after 0.00 sec. (0.00 ticks)
			MIP emphasis: balance optimality and feasibility.
			MIP search method: dynamic search.
			Parallel mode: deterministic, using up to 4 threads.
			Root relaxation solution time = 0.00 sec. (0.06 ticks)

					Nodes                                         Cuts/
			   Node  Left     Objective  IInf  Best Integer    Best Bound    ItCnt     Gap

			*     0+    0                            0.0000                            ---
				  0     0        cutoff              0.0000        0.0000        6    0.00%
				  0     0        cutoff              0.0000        0.0000        6    0.00%
			Elapsed time = 0.03 sec. (0.14 ticks, tree = 0.00 MB, solutions = 1)

			Root node processing (before b&c):
			  Real time             =    0.03 sec. (0.14 ticks)
			Parallel b&c, 4 threads:
			  Real time             =    0.00 sec. (0.00 ticks)
			  Sync time (average)   =    0.00 sec.
			  Wait time (average)   =    0.00 sec.
									  ------------
			Total (root+branch&cut) =    0.03 sec. (0.14 ticks)
			Objective: 0.0
			---------sleep for: 29 seconds-----------
			---------next iteration: Tue Nov 03 13:33:45 CET 2015 -----------
			-------------Tasks running -------------------
			--------------y results-----------------------
			----------VM should be started or running:----
			-------------Tasks to be started -------------
			----------------------------------------------

```