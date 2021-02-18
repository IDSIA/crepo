from pathlib import Path


## default values
prj_path = Path("../").resolve()
res_folder = f"{prj_path}/results/"
log_folder = f"{prj_path}/logs/"
model_folder = f"{prj_path}/networks/"
java_src = f"{prj_path}/src/main/java/ch/idsia/"

jar_file = f"{prj_path}/target/experiments.jar"