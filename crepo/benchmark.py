import os
from pathlib import Path
from datetime import datetime


from crepo.gitconnection import request
from crepo.utils import singleton, write_file, gettempdir
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
    save_jar(gettempdir())


def get_benchmark_data(reload:bool=False) -> pd.DataFrame:
    if reload: info.local_df = None
    if info.local_df is None:
        info.local_df = pd.read_csv(io.StringIO(request("data/exact_inference.csv")))
    return info.local_df

def save_jar(folder: str, reload:bool=False):
    if reload: info.local_jar = None
    if info.local_jar is None:
        content = request("updater/lib/crepo.jar?raw=true", False)
        info.local_jar = os.path.join(Path(folder), Path(f"/crepo.jar"))
        write_file(info.local_jar, content, True)


def get_model(label:str) -> str:
    return None




#download_metadata()
