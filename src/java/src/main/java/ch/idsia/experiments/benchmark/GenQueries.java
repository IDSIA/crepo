package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.credal.linear.separate.SeparateHalfspaceFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactor;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.model.graphical.GraphicalModel;
import ch.idsia.crema.preprocess.CutObserved;
import ch.idsia.crema.preprocess.RemoveBarren;
import ch.idsia.crema.utility.ArraysUtil;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class GenQueries {

	public static boolean checkALP = false;

	public static void main(String[] args) throws IOException, InterruptedException {

		ArrayList<String> fail = new ArrayList<>();

		String prj_folder = ".";
		String modelFolder = prj_folder+"/networks/vmodel/";
		String queryFolder = prj_folder+"/queries/";

		for(String modelFile : getFiles(modelFolder)) {
			System.out.println("Reading "+modelFile);

			DAGModel<VertexFactor> model = IO.readUAI(modelFile); // TODO: check if the type of factor is correct
			List<String> cond = getCondQueries(model);
			List<String> marg = getMargQueries(model);

			if(cond.isEmpty() && marg.isEmpty()){
				System.out.println("FAILED TO FIND QUERY");
				fail.add(modelFile);
			}else{
				saveQueryCSV(queryFolder, "cond", modelFile, cond);
				saveQueryCSV(queryFolder, "marg", modelFile, marg);
			}

		}

		System.out.println("Failed\n==============");
		fail.forEach(System.out::println);
	}

	public static void saveQueryCSV(String queryFolder, String prefix, String modelFile, List<String> queries) throws FileNotFoundException {

		if(queries.isEmpty())
			return;

		String content = "size,target,observed\n";
		content+= String.join("\n", queries.toArray(String[]::new));
		content+="\n";


		String filename = modelFile.substring(modelFile.lastIndexOf("/"));
		filename = filename
				.replace(".uai", ".csv")
				.replace("vmodel-",prefix+"-")
				.replace("hmodel-",prefix+"-");

		File csvOutputFile = new File(queryFolder+filename);
		try(PrintWriter pw = new PrintWriter(csvOutputFile)){
			pw.print(content);
		}
	}

	public static List<String> getCondQueries(DAGModel<VertexFactor> model) {
		TIntIntHashMap evidence = new TIntIntHashMap();
		for(int y : model.getLeaves())
			evidence.put(y,0);

		return getQueriesWith(model, model.getRoots(), evidence);
	}

	public static List<String> getMargQueries(DAGModel<VertexFactor> model) {
		return getQueriesWith(model, model.getLeaves(), new TIntIntHashMap());
	}

	public static List<String> getQueriesWith(DAGModel<VertexFactor> model, int[] targetVars, TIntIntHashMap evidence){
		List<ArrayList<String>> sizes = IntStream.rangeClosed(0, model.getVariables().length)
				.mapToObj(i -> new ArrayList<String>())
				.collect(Collectors.toList());

		for(int x : targetVars){
			int s = getInferenceModelSize(model, evidence, x);
			String obsStr = String.join(" ",IntStream.of(evidence.keys()).mapToObj(i->""+i).toArray(String[]::new));
			sizes.get(s).add(s + "," + x + "," + obsStr);
		}

		for(int s=sizes.size()-1; s>=0; s--){
			if(!sizes.get(s).isEmpty()){
				return sizes.get(s);
			}
		}
		return new ArrayList<>();
	}

	private static int getInferenceModelSize(GraphicalModel<VertexFactor> model, TIntIntMap evidence, int target) {
		CutObserved<VertexFactor> cutObserved = new CutObserved<>();
		GraphicalModel<VertexFactor> infModel = cutObserved.execute(model, evidence); // this already makes a copy

		RemoveBarren<VertexFactor> removeBarren = new RemoveBarren<>();
		removeBarren.executeInPlace(infModel, evidence, target);

		return infModel.getVariables().length;
	}

	public static String getMaximalQuery(String vmodelPath) throws IOException, InterruptedException {
		// marginal or conditional query with a maximal inference network
		DAGModel<VertexFactor> model = IO.readUAI(vmodelPath);

		int target = -1;
		int maxSize = 0;
		int observed = -1;

		//[List(double<x,y>), ... List(double<x,y>), ]
		List<ArrayList<int[]>> sizes = IntStream.rangeClosed(0, model.getVariables().length)
				.mapToObj(i-> new ArrayList<int[]>())
				.collect(Collectors.toList());

		for(int x : model.getVariables()){
			for(int y: ArraysUtil.append(model.getVariables(), -1)) {
				if(x!=y){
					TIntIntHashMap evidence = new TIntIntHashMap();
					if(y!=-1)
						evidence.put(y,0);
					int s = getInferenceModelSize(model, evidence, x);
					sizes.get(s).add(new int[]{x,y});
				}
			}
		}

		for(int i = sizes.size()-1; i>=0; i--){
			System.out.println(i);

			for(Object p : sizes.get(i)){
				target = ((int[])p)[0];
				observed =  ((int[])p)[1];
				System.out.println(target+"|"+observed);

				if(isApproxEval(vmodelPath, target, observed)){
					String queryStr = target +",";
					if(observed != -1){
						queryStr += observed;
					}
					queryStr+=","+i;

					System.out.println(maxSize + "/" + model.getVariables().length + " nodes:\t" + queryStr);
					return queryStr;
				}
			}
		}

		return null;
	}

	public static boolean isApproxEval(String vmodelPath, int target, int observed) throws IOException, InterruptedException {
		String hmodelPath = vmodelPath.replace("vmodel","hmodel");
		DAGModel<SeparateHalfspaceFactor> hmodel = IO.read(hmodelPath);

		boolean eval = true;

		TIntIntHashMap evidence = new TIntIntHashMap();
		if (observed != -1)
			evidence.put(observed, 0);


		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Boolean> future = executorService.submit(new Evaluation<>(hmodel, target, evidence));
		try {
			eval = future.get(30L, TimeUnit.MINUTES);
		} catch (ExecutionException | TimeoutException e) {
			e.printStackTrace();
			eval = false;
		}
		return eval;
	}

	public static List<String> getFiles(String folder) throws IOException {
		return StreamSupport
				.stream(Files.newDirectoryStream(
						Paths.get(folder),
						path -> path.toString().endsWith(".uai")
				).spliterator(), false)
				.map(Path::toString)
				.collect(Collectors.toList());
	}

}


