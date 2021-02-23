import requests

from crepobenchmark import __version__ as vtag

from crepobenchmark.utils import singleton


@singleton
class GitConnection(object):

    def __init__(self):
        self._base_url = f"https://raw.githubusercontent.com/IDSIA-papers/crepo/{vtag}/"

    @property
    def base_url(self):
        return self._base_url

# singleton instance
git_connect = GitConnection()

def request(remote_path:str, decode:bool = True):
    url = git_connect._base_url+remote_path
    s = requests.get(url)
    if not s.ok:
        raise ConnectionError(f"Error downloading {url}. Status code {s.status_code}")
    return s.content.decode('utf-8') if decode else s.content

def check():
    request = requests.get(git_connect._base_url + "README.md")
    return request.status_code == 200

