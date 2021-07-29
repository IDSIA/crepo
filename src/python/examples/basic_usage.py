import crepobenchmark as crb

# Download the inference results
data = crb.get_benchmark_data()

crb.info.java_bin = "/Library/Java/JavaVirtualMachines/openjdk-12.0.1.jdk/Contents/Home/bin/java"

# Get a description of the data
crb.describe()

# Save a model from the repository
crb.save_model("vs_n4_mID2_mD6_mV4_nV2-1", "model.uai")

# Run exact inference
crb.run_crema("model.uai", target=0)