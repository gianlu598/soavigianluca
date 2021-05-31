/* Generated by AN DISI Unibo */ 
package it.unibo.sonar

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonar ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var simulate = true
		   lateinit var firstActorInPipe : ActorBasic 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("sonar START")
						discardMessages = true
						solve("consult('sonar2021ConfigKb.pl')","") //set resVar	
						solve("simulate(X)","") //set resVar	
						println(currentSolution)
						 val x = getCurSol("X").toString() 
								   simulate = ( x == "on")	
								   println( "simulate=$simulate" )
						  if( simulate ) firstActorInPipe = sysUtil.getActor("sonarsimulator")!!  //generates simulated data
									else firstActorInPipe           = sysUtil.getActor("sonardatasource")!!  //generates REAL data
						 			firstActorInPipe.
										subscribeLocalActor("datacleaner"). 		//removes 'wrong' data''
										//subscribeLocalActor("datalogger").		    //logs (shows) the data generated by the sonar
						  				subscribeLocalActor("sonar").				//handles sonarrobot but does not propagate ...
						 				subscribeLocalActor("distancefilter").		//propagates led event
						 				subscribeLocalActor("led")					//led
						  				//subscribeLocalActor("sonar")  
						if(  simulate  
						 ){forward("simulatorstart", "simulatorstart(ok)" ,"sonarsimulator" ) 
						}
						else
						 {forward("sonarstart", "sonarstart(ok)" ,"sonardatasource" ) 
						 }
					}
					 transition(edgeName="t00",targetState="handleSonarData",cond=whenEvent("sonar"))
				}	 
				state("handleSonarData") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("distance(V)"), Term.createTerm("distance(D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 val D = payloadArg(0) 
								  		 	   //val ev = MsgUtil.buildEvent(name,"sonarrobot","sonar($D)")
								  		 	   //emit( ev, false  )  //Not emit for me (but not applies to MQTT)
								  		 	   //val distanceInt: Int = D.toInt()
								emit("sonarrobot", "sonar($D)" ) 
						}
					}
					 transition(edgeName="t01",targetState="handleSonarData",cond=whenEvent("sonar"))
				}	 
			}
		}
}
