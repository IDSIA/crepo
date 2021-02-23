import os
from pathlib import Path

from crepobenchmark.gitconnection import request
from crepobenchmark.utils import singleton, write_file, gettempdir, exec_bash
import io


import pandas as pd


@singleton
class metainfo:
    local_jar = None
    local_df = None

    def reset(self):
        self.local_jar = None
        self.local_df = None

info = metainfo()

def download_metadata():
    info.reset()
    get_benchmark_data()
    save_jar()


def get_benchmark_data(reload:bool=False) -> pd.DataFrame:
    if reload: info.local_df = None
    if info.local_df is None:
        info.local_df = pd.read_csv(io.StringIO(request("data/full.csv")))
    return info.local_df

def save_jar(folder: str = None, reload:bool=False):
    if reload: info.local_jar = None
    if info.local_jar is None:
        folder = folder or gettempdir()
        content = request("src/java/lib/crepo.jar?raw=true", False)
        info.local_jar = os.path.join(Path(folder), Path(f"crepo.jar"))
        write_file(info.local_jar, content, True)

def get_model(label:str) -> str:
    return request(get_filename(label)[1:], decode=True)

def get_filename(label:str) -> str:
    return info.local_df[info.local_df["label"]==label]["filename"].iloc[0]


def save_model(label, filename):
    content = get_model(label)
    write_file(filename, content, False)





def run_crema(filename: str, target: int = 0, observed: str = "1", method: str = "cve",
              measure_time: bool = True, runs: int = 1, warmups: int = 0, timeout: int = 600, logfile: str = None,
              **kwargs):

    save_jar()
    observed = str(observed)
    javafile = f"ch.idsia.RunCrema"
    cmd = "-t" if measure_time else ""
    cmd += f" --method={method} -r {int(runs)} -w {int(warmups)} -x {int(target)}"
    if len(observed) > 0: cmd += f" -y {observed}"
    cmd += f" --timeout={timeout} {filename}"
    cmd += f" --log={logfile}" if logfile is not None else ""
    print(cmd)
    cmd = f"java -cp {info.local_jar} {javafile} {cmd}"
    output = exec_bash(cmd)
    exec(output[-1])
    return locals()["results"]


if __name__ == "__main__":
    download_metadata()
    data = get_benchmark_data()

    modelname = data.iloc[0]["label"]

    print(get_model(modelname))

    save_model(modelname, "model.uai")

    res = run_crema("model.uai", target=0)
