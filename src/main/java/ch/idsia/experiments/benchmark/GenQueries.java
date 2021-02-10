package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.inference.ve.CredalVariableElimination;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.utility.ArraysUtil;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenQueries {
	public static void main(String[] args) throws IOException {


		String prj_folder = "/Users/rcabanas/GoogleDrive/IDSIA/causality/dev/idsia-papers/2021-SIPTA-crema/";
		String modelFolder = prj_folder+"/networks/vmodel/";
		String queryFolder = prj_folder+"/queries/";


		for(String modelFile : getFiles(modelFolder)) {
			System.out.println("Reading "+modelFile);
			DAGModel model = (DAGModel) IO.read(modelFile);
			String queryStr = getMaximalQuery(model);
			System.out.println(queryStr);
			saveQueryCSV(queryFolder, modelFile, queryStr);
		}

	}

	public static void saveQueryCSV(String queryFolder, String modelFile, String queryStr) throws FileNotFoundException {
		String content = "target,observed\n";
		content+=queryStr;
		content+="\n";


		String filename = modelFile.substring(modelFile.lastIndexOf("/"));
		filename = filename
				.replace(".uai", ".csv")
				.replace("vmodel-","query-")
				.replace("hmodel-","query-");

		File csvOutputFile = new File(queryFolder+filename);
		try(PrintWriter pw = new PrintWriter(csvOutputFile)){
			pw.print(content);
		}
	}

	public static String getMaximalQuery(DAGModel model) {
		// marginal or conditional query with a maximal inference network

		CredalVariableElimination ve = new CredalVariableElimination(model);

		int target = -1;
		int maxSize = 0;
		int observed = -1;

		for(int x : model.getVariables()){
			for(int y: ArraysUtil.append(model.getVariables(), -1)) {
				if(x!=y){

					TIntIntHashMap evidence = new TIntIntHashMap();
					if(y!=-1)
						evidence.put(y,0);

					int s = ve.getInferenceModel(x, evidence).getVariables().length;

					if (s > maxSize) {
						target = x;
						observed = y;
						maxSize = s;

						if(maxSize==model.getVariables().length)
							break;
					}
				}
			}
		}


		String queryStr = target +",";
		if(observed != -1){
			queryStr += observed;
		}

		System.out.println(maxSize+"/"+model.getVariables().length+" nodes:\t"+queryStr);
		return queryStr;
	}

	public static List<String> getFiles(String folder) throws IOException {
		return StreamSupport
				.stream(Files.newDirectoryStream(
						Paths.get(folder),
						path -> path.toString().endsWith(".uai")
				).spliterator(), false)
				.map(f -> f.toString())
				.collect(Collectors.toList());
	}
}
