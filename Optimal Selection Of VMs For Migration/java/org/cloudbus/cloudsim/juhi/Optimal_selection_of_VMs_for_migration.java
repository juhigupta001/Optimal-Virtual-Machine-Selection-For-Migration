package org.cloudbus.cloudsim.juhi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class Optimal_selection_of_VMs_for_migration {

	static int n = 5;
	static int lt = 0;
	static int ut = 0;

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	public static void main(String[] args) {

		ArrayList<Long> VmCreationTimeList = new ArrayList<>();
		int arr[] = { 12, 16, 123, 345 };
		median(arr);
		System.out.println("Lower threshold= " + lt);
		System.out.println("Upper threshold= " + ut);

		Log.printLine("Starting CloudSim...");

		try {

			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Create Datacenters

			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Create one virtual machine
			vmlist = new ArrayList<Vm>();

			// VM description
			int vmid = 0;
			int mips[] = { 250, 300, 150, 230, 240 };
			long[] size = { 10000, 10000, 10000, 10000, 10000 }; // image size
																	// (MB)
			int ram = 2048; // vm memory (MB)
			long[] bw = { 1000, 500, 300, 1000, 1000 };
			int pesNumber = 1; // number of cpus
			String vmm = "Xen"; // VMM name

			// create five VMs
			Vm vm1 = new Vm(vmid, brokerId, mips[0], pesNumber, ram, bw[0], size[0], vmm,
					new CloudletSchedulerTimeShared());

			// the second VM will have twice the priority of VM1 and so will
			// receive twice CPU time
			vmid++;
			Vm vm2 = new Vm(vmid, brokerId, mips[1], pesNumber, ram, bw[1], size[1], vmm,
					new CloudletSchedulerTimeShared());

			vmid++;
			Vm vm3 = new Vm(vmid, brokerId, mips[2], pesNumber, ram, bw[2], size[2], vmm,
					new CloudletSchedulerTimeShared());

			vmid++;
			Vm vm4 = new Vm(vmid, brokerId, mips[3], pesNumber, ram, bw[3], size[3], vmm,
					new CloudletSchedulerTimeShared());

			vmid++;
			Vm vm5 = new Vm(vmid, brokerId, mips[4], pesNumber, ram, bw[4], size[4], vmm,
					new CloudletSchedulerTimeShared());

			// add the VMs to the vmList
			vmlist.add(vm1);
			VmCreationTimeList.add(System.currentTimeMillis());
			vmlist.add(vm2);
			VmCreationTimeList.add(System.currentTimeMillis() + 2);
			vmlist.add(vm3);
			VmCreationTimeList.add(System.currentTimeMillis() + 4);
			vmlist.add(vm4);
			VmCreationTimeList.add(System.currentTimeMillis() + 6);
			vmlist.add(vm5);
			VmCreationTimeList.add(System.currentTimeMillis() + 8);

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			// Create two Cloudlets
			cloudletList = new ArrayList<Cloudlet>();

			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			Cloudlet cloudlet1 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel,
					utilizationModel, utilizationModel);
			cloudlet1.setUserId(brokerId);

			id++;
			Cloudlet cloudlet2 = new Cloudlet(id, length + 200000, pesNumber, fileSize + 2000, outputSize,
					utilizationModel, utilizationModel, utilizationModel);
			cloudlet2.setUserId(brokerId);

			id++;
			Cloudlet cloudlet3 = new Cloudlet(id, length + 200000, pesNumber, fileSize + 2000, outputSize,
					utilizationModel, utilizationModel, utilizationModel);
			cloudlet3.setUserId(brokerId);

			id++;
			Cloudlet cloudlet4 = new Cloudlet(id, length + 100000, pesNumber, fileSize + 2000, outputSize,
					utilizationModel, utilizationModel, utilizationModel);
			cloudlet4.setUserId(brokerId);

			id++;
			Cloudlet cloudlet5 = new Cloudlet(id, length, pesNumber, fileSize + 2000, outputSize, utilizationModel,
					utilizationModel, utilizationModel);
			cloudlet5.setUserId(brokerId);

			id++;
			Cloudlet cloudlet6 = new Cloudlet(id, length + 200000, pesNumber, fileSize + 2000, outputSize,
					utilizationModel, utilizationModel, utilizationModel);
			cloudlet6.setUserId(brokerId);

			// add the cloudlets to the list
			cloudletList.add(cloudlet1);
			cloudletList.add(cloudlet2);
			cloudletList.add(cloudlet3);
			cloudletList.add(cloudlet4);
			cloudletList.add(cloudlet5);
			cloudletList.add(cloudlet6);

			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);

			// bind the cloudlets to the vms.
			broker.bindCloudletToVm(cloudlet1.getCloudletId(), vm1.getId());
			broker.bindCloudletToVm(cloudlet2.getCloudletId(), vm2.getId());

			// Starts the simulation
			CloudSim.startSimulation();

			// Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(newList);
			// vm migration
			func(VmCreationTimeList, newList);
			//
			System.out.println("utilisation=" + utilizationModel.getUtilization(1024));
			Log.printLine(" finished!");

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}

	}

	private static void func(ArrayList<Long> vmlist, List<Cloudlet> list) {
		DecimalFormat df2 = new DecimalFormat("#.####");
		// calculate max time for task completion
		HashMap<Integer, Double> map = new HashMap<>();
		for (Cloudlet val : list) {
			int vmid = val.getCloudletId();
			double starttime = val.getExecStartTime();
			double finishtime = val.getFinishTime();
			double tasktime = finishtime - starttime;
			if (!map.containsKey(vmid))
				map.put(vmid, tasktime);
			else
				map.put(vmid, map.get(vmid) + tasktime);

		}

		// decision matrix

		double dm[][] = new double[n][2];
		double maxc1 = Double.MIN_VALUE;
		Double minc1 = Double.MAX_VALUE;
		Double maxc2 = Double.MIN_VALUE;
		Double minc2 = Double.MAX_VALUE;

		// System.out.println(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()));

		for (int i = 0; i < n; i++) {

			dm[i][0] = System.currentTimeMillis() - vmlist.get(i);
			dm[i][1] = map.get(i) - dm[i][0];
			df2.format(dm[i][1]);
			maxc1 = Math.max(maxc1, dm[i][0]);
			minc1 = Math.min(minc1, dm[i][0]);
			maxc2 = Math.max(maxc2, dm[i][1]);
			minc2 = Math.min(minc2, dm[i][1]);
		}
		System.out.println("Decision matrix");
		System.out.println("----------------");
		display(dm);
		System.out.println("----------------");
		System.out.println("Normalised decision matrix");

		// decision matrix with normalised fuzzy value

		int newmax = 1;
		int newmin = 0;
		double dif1 = maxc1 - minc1;
		double dif2 = maxc2 - minc2;

		for (int i = 0; i < n; i++) {
			double x = dm[i][0];
			double y = dm[i][1];
			dm[i][0] = (((x - minc1) / dif1) * (newmax - newmin)) - newmin;
			dm[i][1] = (((y - minc2) / dif2) * (newmax - newmin)) - newmin;

		}

		System.out.println("----------------");
		display(dm);
		System.out.println("----------------");

		/////// Triangular Membership function
		HashMap<String, ArrayList<Double>> func = triangularMembershipFunction();

		/////// DECISION MATRIX WITH LINGUISTIC VARIABLES
		String dmlvar[][] = new String[n][2];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 2; j++) {
				double val = dm[i][j];
				dmlvar[i][j] = search(val, func);

			}
		}
		System.out.println("DECISION MATRIX WITH LINGUISTIC VARIABLES");
		System.out.println("------------------");
		display2(dmlvar);
		System.out.println("-------------------");

		///////// DECISION MATRIX WITH FUZZY TRIANGULAR MEMBERSHIP FUNCTION
		HashMap<String, Integer> freq1 = new HashMap<>();
		HashMap<String, Integer> freq2 = new HashMap<>();

		System.out.println("DECISION MATRIX WITH FUZZY TRIANGULAR MEMBERSHIP FUNCTION");
		System.out.println("C1                          C2");
		String str1 = "", str2 = "";
		int maxlen1 = 0, maxlen2 = 0;
		for (int i = 0; i < n; i++) {
			String s1 = dmlvar[i][0];
			String s2 = dmlvar[i][1];
			System.out.println(func.get(s1) + "        " + func.get(s2));
			freq1.put(s1, freq1.getOrDefault(s1, 0) + 1);
			freq2.put(s2, freq2.getOrDefault(s2, 0) + 1);
			if (freq1.get(s1) > maxlen1) {
				maxlen1 = freq1.get(s1);
				str1 = s1;
			}
			freq2.put(s1, freq2.getOrDefault(s1, 0) + 1);
			if (freq2.get(s2) > maxlen2) {
				maxlen2 = freq2.get(s1);
				str2 = s2;
			}

		}

		System.out.println("----------------");
		/////// The weight determined for each criteria deﬁned
		ArrayList<Double> c1 = func.get(str1);
		ArrayList<Double> c2 = func.get(str2);

		System.out.println("The weight determined for each criteria deﬁned is: ");
		System.out.println("C1 -> " + c1);
		System.out.println("C2 -> " + c2);
		System.out.println("-----------------");

		/////// FUZZY DECISION MATRIX WEIGHTED
		ArrayList<Double> dmw[][] = new ArrayList[n][2];
		double max1 = 0, min1 = 0, max2 = 0, min2 = 0;
		int id1 = 0, id2 = 0, id3 = 0, id4 = 0;
		System.out.println("FUZZY DECISION MATRIX WEIGHTED");
		System.out.println("C1                          C2");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 2; j++) {
				ArrayList<Double> temp = new ArrayList<>();
				ArrayList<Double> l1 = func.get(dmlvar[i][j]);
				for (int k = 0; k < l1.size(); k++) {
					double t = 0;
					if (j == 0) {
						t = l1.get(k) * c1.get(k);
						System.out.print(df2.format(t) + " ");
						if (k == l1.size() - 1 && t > max1) {
							max1 = t;
							id1 = i;
						}
						if (k == 0 && t < min1) {
							min1 = t;
							id2 = i;
						}

					}
					if (j == 1) {
						t = l1.get(k) * c2.get(k);
						System.out.print(df2.format(t) + " ");
						if (k == l1.size() - 1 && t > max2) {
							max2 = t;
							id3 = i;
						}
						if (k == 0 && t < min2) {
							min2 = t;
							id4 = i;
						}
					}
					temp.add(t);
				}
				dmw[i][j] = temp;
				System.out.print("     ");
			}
			System.out.println();

		}
		System.out.println("-------------");

		///////////// ﬁnding out the shortest distance from PIS and thefarthest
		///////////// distance from NIS, deﬁned as A+ and A −respectively
		System.out.println(
				"The shortest distance from PIS and the farthest distance from NIS, deﬁned as A+ and A −respectively");
		ArrayList<Double> C1Ap = new ArrayList<>();
		ArrayList<Double> C1An = new ArrayList<>();
		ArrayList<Double> C2Ap = new ArrayList<>();
		ArrayList<Double> C2An = new ArrayList<>();

		for (int i = 0; i < 3; i++)
			C1Ap.add(func.get(dmlvar[id1][0]).get(i) * c1.get(i));
		for (int i = 0; i < 3; i++)
			C1An.add(func.get(dmlvar[id2][0]).get(i) * c1.get(i));
		for (int i = 0; i < 3; i++)
			C2Ap.add(func.get(dmlvar[id3][1]).get(i) * c2.get(i));
		for (int i = 0; i < 3; i++)
			C2An.add(func.get(dmlvar[id4][1]).get(i) * c2.get(i));

		// For C1
		// v1p->max of 3rd column
		// v2p->min of 1st column

		System.out.println("For C1");
		System.out.println("A+  ->" + C1Ap);
		System.out.println("A-  ->" + C1An);
		System.out.println("--------");
		System.out.println("For C2");
		System.out.println("A+  ->" + C2Ap);
		System.out.println("A-  ->" + C2An);
		////////////// The separation measures di+ and di-
		double dip1 = 0, dip2 = 0, din1 = 0, din2 = 0;

		double sm[][] = new double[n][2];// SEPARATION MEASURES MATRIX
		for (int i = 0; i < n; i++) {
			ArrayList<Double> temp1 = dmw[i][0];
			ArrayList<Double> temp2 = dmw[i][1];
			for (int k = 0; k < 3; k++) {
				dip1 = +Math.abs(temp1.get(k) - C1Ap.get(k));
				dip2 = +Math.abs(temp1.get(k) - C2Ap.get(k));
				din1 = +Math.abs(temp2.get(k) - C2An.get(k));
				din2 = +Math.abs(temp2.get(k) - C1An.get(k));
			}
			sm[i][0] = dip1 + dip2;
			sm[i][1] = din1 + din2;

		}
		System.out.println("SEPARATION MEASURES MATRIX");
		display(sm);
		System.out.println("---------------");

		//////////////// TOPSIS rank
		Double finalrank[] = new Double[n];
		for (int i = 0; i < n; i++)
			finalrank[i] = sm[i][1] / (sm[i][0] + sm[i][1]);
		Arrays.sort(finalrank, Collections.reverseOrder());
		Double max = finalrank[0];
		//////////// fINAL ANS
		ArrayList<Integer> anslist = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if (finalrank[i] == max)
				anslist.add(i);

		}
		System.out.println("Relative Closeness values of vms");
		for (int i = 0; i < n; i++)
			System.out.println("VM" + i + "->" + finalrank[i]);
		System.out.println("---------------");
		System.out.println("VM Chosed for Migration");
		System.out.println("VM->" + anslist);

	}

	private static String search(double k, HashMap<String, ArrayList<Double>> map) {
		for (String val : map.keySet()) {
			ArrayList<Double> list = map.get(val);
			if (k >= list.get(0) && k <= list.get(2))
				return val;
		}
		return "";

	}

	private static HashMap<String, ArrayList<Double>> triangularMembershipFunction() {
		HashMap<String, ArrayList<Double>> map = new HashMap<>();
		ArrayList<Double> list;
		list = new ArrayList<>();
		list.add(0.0);
		list.add(0.10);
		list.add(0.25);
		map.put("VL", list);
		list = new ArrayList<>();
		list.add(0.15);
		list.add(0.30);
		list.add(0.45);
		map.put("L", list);
		list = new ArrayList<>();
		list.add(0.35);
		list.add(0.50);
		list.add(0.65);
		map.put("M", list);
		list = new ArrayList<>();
		list.add(0.55);
		list.add(0.70);
		list.add(0.85);
		map.put("H", list);
		list = new ArrayList<>();
		list.add(0.75);
		list.add(0.90);
		list.add(1.0);
		map.put("VH", list);
		System.out.println("Triangular Membership Function Used");
		for (String val : map.keySet()) {
			System.out.println(val + " -> " + map.get(val));
		}
		System.out.println("----------");

		return map;

	}

	private static void display(double a[][]) {
		DecimalFormat df2 = new DecimalFormat("#.####");
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < 2; j++)
				System.out.print(df2.format(a[i][j]) + "    ");
			System.out.println();
		}
	}

	private static void display2(String a[][]) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < 2; j++)
				System.out.print(a[i][j] + " ");
			System.out.println();
		}
	}

	private static Datacenter createDatacenter(String name) {

		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.

		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store
																// Pe id and
																// MIPS Rating

		// 4. Create Hosts with its id and list of PEs and add them to the list
		// of machines
		int hostId = 0;
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList,
				new VmSchedulerTimeShared(peList))); // This is our first
														// machine

		// create another machine in the Data center
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));

		// PowerHost p= new PowerHostUtilizationHistory(hostId,new
		// RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage,
		// peList, new VmSchedulerTimeShared(peList));
		// System.out.println("ans"+p.getUtilizationOfCpu());

		hostId++;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // This is our second
														// machine

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // This is our second
														// machine

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // This is our third
														// machine

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // This is our fourth
														// machine

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are
																		// not
																		// adding
																		// SAN
																		// devices
																		// by
																		// now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
				cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker() {

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");
				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}

	}

	// median algo
	public static void median(int arr[]) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					int temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}

		int median = arr.length / 2;

		int set1[] = new int[median];
		int set2[] = new int[median];

		if (arr.length % 2 == 0) {
			int k = 0;
			for (int i = 0; i < median; i++) {
				set1[k] = arr[i];
				k++;
			}
			k = 0;
			for (int i = 0; i < median; i++) {
				set2[k] = arr[i + median];
				k++;
			}

		}

		else {
			int k = 0;
			for (int i = 0; i < median; i++) {
				set1[k] = arr[i];
				k++;
			}
			k = 0;
			for (int i = 1; i < median; i++) {
				set2[k] = arr[i + median];
				k++;
			}

		}

		lt = set1[median / 2];
		ut = set2[median / 2];

		System.out.println("Under Utilised Hosts  ");
		for (int i = 0; i < arr.length; i++) {
			if (isUnderUtilised(arr[i])) {
				System.out.print("HostNo " + i);
			}
		}
		System.out.println();
		System.out.println("Over Utilised Hosts  ");
		for (int i = 0; i < arr.length; i++) {
			if (isOverUtilised(arr[i])) {
				System.out.print("HostNo " + i);
			}
		}

		System.out.println();

	}

	public static boolean isUnderUtilised(int ca) {

		if (ca < lt)
			return true;
		else
			return false;
	}

	public static boolean isOverUtilised(int ca) {
		if (ca > ut)
			return true;
		else
			return false;
	}

}
