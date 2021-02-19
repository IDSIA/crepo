import requests

from crepo.utils import singleton


@singleton
class GitConnection(object):

    def __init__(self):
        self._base_url = "https://raw.githubusercontent.com/IDSIA-papers/crepo/0.0.1/"

    @property
    def base_url(self):
        return self._base_url

# singleton instance
git_connect = GitConnection()

def request(remote_path:str, decode:bool = True):
    s = requests.get(git_connect._base_url+remote_path).content
    return s.decode('utf-8') if decode else s

def check():
    request = requests.get(git_connect._base_url + "README.md")
    return request.status_code == 200

