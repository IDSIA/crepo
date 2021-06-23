package ch.idsia;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.GenericFactor;
import ch.idsia.crema.factor.convert.VertexToInterval;
import ch.idsia.crema.factor.credal.linear.interval.IntervalFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactor;
import ch.idsia.crema.inference.approxlp.CredalApproxLP;
import ch.idsia.crema.inference.ve.CredalVariableElimination;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.utility.GraphUtil;
import ch.idsia.crema.utility.InvokerWithTimeout;
import ch.idsia.crema.utility.hull.ConvexHull;
import gnu.trove.map.hash.TIntIntHashMap;
import org.apache.commons.lang3.time.StopWatch;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;


/*
-t --method=cve -r 2 -w 1 -x 0 -y 1 --log=./logs/logfile.log ./networks/vmodel/vmodel-mult_n4_mID2_mD6_mV4_nV4-3.uai
-t --method=approxlp -r 2 -w 1 -x 0 -y 1 --log=./logs/logfile.log ./networks/hmodel/hmodel-mult_n4_mID2_mD6_mV4_nV4-3.uai
/Users/rcabanas/GoogleDrive/IDSIA/causality/dev/idsia-papers/2021-SIPTA-crema/networks/hmodel/hmodel-mult_n4_mID2_mD6_mV4_nV4-3.uai


 */

public class RunCrema implements Runnable {

	private static String argStr;

	private static Logger logger;

	private GenericFactor result;
//	private GenericFactor exactResult;
	private Long time = 0L;

	private static String errMsg = "";

	enum InferenceMethod {approxlp, intervallp, cve, cve_ch, cve_ch10, cve_ch5}

	// Set as hash map
	private final static List<InferenceMethod> VE_METHODS = List.of(
			InferenceMethod.cve,
			InferenceMethod.cve_ch,
			InferenceMethod.cve_ch5,
			InferenceMethod.cve_ch10
	);

	private final static List<InferenceMethod> LP_METHODS = List.of(
			InferenceMethod.approxlp,
			InferenceMethod.intervallp
	);

	private final static List<InferenceMethod> APPROX_METHODS = List.of(
			InferenceMethod.approxlp,
			InferenceMethod.intervallp,
			InferenceMethod.cve_ch5,
			InferenceMethod.cve_ch10
	);
	private final static Map<InferenceMethod, ConvexHull> CH_METHODS = Map.ofEntries(
			Map.entry(InferenceMethod.cve_ch, ConvexHull.LP_CONVEX_HULL),
			Map.entry(InferenceMethod.cve_ch5, ConvexHull.REDUCED_HULL_5),
			Map.entry(InferenceMethod.cve_ch10, ConvexHull.REDUCED_HULL_10)
	);


	/// Command line

	@CommandLine.Spec
	CommandLine.Model.CommandSpec spec;

	@Option(names = {"-m", "--method"}, required = true, description = "Inference method: ${COMPLETION-CANDIDATES}")
	private InferenceMethod method;

	@Option(names = {"-x", "--target"}, required = true, description = "Target variable ID.")
	private int target;

	@Option(names = {"-y", "--observed"}, arity = "0..*", description = "Space-separated list of observed variables IDs.")
	private int[] observed = new int[]{};

	@Option(names = {"-t", "--time"}, description = "Measure time")
	private boolean measureTime;

//	@Option(names = {"-e", "--error"}, description = "Measure error")
//	private boolean measureError;

	@Option(names = {"-w", "--warmups"}, description = "Number of warmups (which are not measured). Default is 0.")
	public void setWarmups(int w) {
		if (w < 0) wrongParam("The number of warmups cannot be negative.");
		warmups = w;
	}

	private int warmups = 0;

	@Option(names = {"-r", "--runs"}, description = "Number of runs. Default is 1.")
	public void setRuns(int r) {
		if (r < 1) wrongParam("The number of runs cannot be lower than 1.");
		runs = r;
	}

	private int runs = 1;

	@Option(names = {"-T", "--timeout"}, description = "Timeout in seconds. Default is 3600.")
	private long timeout = 3600;

	@Option(names = {"-l", "--log"}, description = "Log file path. If not specified, messages are shown on standard output.")
	String logFile;

	@Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
	private boolean helpRequested;


	@Parameters(description = "Model path in UAI format.")
	private String modelPath;

	private void wrongParam(String msg) {
		throw new CommandLine.ParameterException(spec.commandLine(), msg);
	}

	public static void main(String[] args) {
		argStr = String.join(";", args);
		CommandLine.run(new RunCrema(), args);
		if (errMsg.isEmpty())
			System.exit(-1);
	}

	@Override
	public void run() {
		try {
			setUp();
			logger.info("Input args: " + argStr);
			experiments();
		} catch (Exception | Error e) {
			errMsg = e.toString();
			logger.severe(errMsg);
			//e.printStackTrace();
		} finally {
			processResults();
		}
	}

	private void experiments() throws IOException, InterruptedException, TimeoutException, ExecutionException {
		int targetVar = target;
		TIntIntHashMap evid = new TIntIntHashMap();
		if(observed != null && observed.length > 0)
			for (int y : observed)
				evid.put(y, 0);

		logger.info("Starting experiments");

		// Load the model
		final DAGModel<VertexFactor> model = IO.readUAI(modelPath); // TODO: this assumes that only vertex-based model are loaded
		logger.info("Loaded model " + model.getNetwork());

		final Callable<GenericFactor> task;

		// Set up the inference engine
		if (VE_METHODS.contains(method)) {
			final CredalVariableElimination inf = new CredalVariableElimination();

			// Set convex hull
			if (CH_METHODS.containsKey(method))
				inf.setConvexHullMarg(CH_METHODS.get(method));

			// CVE works only on VertexFactor models
			task = () -> inf.query(model, evid, targetVar);

		} else if (LP_METHODS.contains(method)) {
			if (method == InferenceMethod.intervallp) {
				DAGModel<IntervalFactor> imodel = new DAGModel<>();
				GraphUtil.copy(model.getNetwork(), imodel.getNetwork());

				for (int x : imodel.getVariables())
					imodel.setFactor(x, new VertexToInterval().apply(model.getFactor(x), x));

				// CALP works with any FilterableFactor, there we assume an IntervalFactor model...
				final CredalApproxLP<IntervalFactor> inf = new CredalApproxLP<>();
				task = () -> inf.query(imodel, evid, targetVar);

			} else {
				// ...there instead we assume a VertexFactor model
				final CredalApproxLP<VertexFactor> inf = new CredalApproxLP<>();
				task = () -> inf.query(model, evid, targetVar);
			}
		} else {
			throw new IllegalArgumentException("Unknown inference method");
		}

		final InvokerWithTimeout<GenericFactor> invoker = new InvokerWithTimeout<>();
		final StopWatch watch = new StopWatch();

		// Run the warmup iterations
		for (int i = 1; i <= warmups; i++) {
			watch.reset();
			watch.start();
			invoker.run(task, timeout);
			watch.stop();
			logger.info("Warmup iteration " + i + "/" + warmups + " (" + watch.getTime() + " ms.)");
		}

		// Run the measurable experiments
		time = 0L;
		for (int i = 1; i <= runs; i++) {
			watch.reset();
			watch.start();

			result = invoker.run(task, timeout);
			watch.stop();
			time += watch.getTime();
			logger.info("Measurable iteration " + i + "/" + runs + " (" + watch.getTime() + " ms.)");
		}

		/*
		// If needed, run the exact
		if(measureError) {
			exactResult = result;
			if(APPROX_METHODS.contains(method)) {
				logger.info("Running exact inference");
				// Convert model to V-space (if not VE-based)
				DAGModel exactModel = model;
				if(method == InferenceMethod.approxlp)
					exactModel = Convert.HmodelToVmodel(model);
				// Run the exact inference
				exactResult = evaluate(exactModel, InferenceMethod.cve);

			}
		}
		 */
	}

	private void processResults() {

		logger.info("Processing results");

		String msg = "results=dict(";

		ArrayList<String> results = new ArrayList<>();

		if (measureTime)
			results.add("time=" + (((float) time) / runs));

		if (errMsg.length() == 0) {
			IntervalFactor iresult;
			if (result instanceof IntervalFactor) {
				iresult = (IntervalFactor) result;
			} else if (result instanceof VertexFactor) {
				iresult = new VertexToInterval().apply((VertexFactor) result, target);
			} else {
				throw new IllegalStateException("Cannot convert results");
			}

			int cardTarget = iresult.getDomain().getCombinations();

			IntervalFactor finalIresult = iresult;
			results.add(
					"interval_result=[" + String.join(",",
							IntStream.range(0, cardTarget)
									.mapToObj(i -> finalIresult.getLower(0)[i] + "," + finalIresult.getUpper(0)[i])
									.toArray(String[]::new)) + "]");

			results.add("err_msg=''");
		} else {
			results.add("err_msg='" + errMsg + "'");
			results.add("interval_result=[]");
		}

		results.add("arg_str='" + argStr + "'");
		msg += String.join(",", results.toArray(String[]::new));
		msg += ")";
		logger.info(msg);
		System.out.println(msg);
	}

	private void setUp() {
		util.disableWarning();

		logger = Logger.getLogger("MyLog");
		FileHandler fh = null;

		// This block configure the logger with handler and formatter
		try {
			if (logFile == null)
				logFile = File.createTempFile("RunCrema", ".log").getAbsolutePath();

			System.out.println(logFile);
			fh = new FileHandler(logFile, true);

		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.addHandler(fh);
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF_%1$tT][%4$s][java] %5$s%6$s%n");
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
		logger.info("Saving log to: " + logFile);
	}

}