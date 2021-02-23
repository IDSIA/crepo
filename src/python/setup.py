
from setuptools import setup

import os
import re
import sys


if sys.version_info < (3, 4):
    sys.exit('Python < 3.4 is not supported')


# get abs path from this folder name
here = os.path.dirname(os.path.abspath(__file__))

# open __init__.py, where version is specified
with open(os.path.join(here, 'crepobenchmark', '__init__.py')) as f:
    txt = f.read()

# try to read it from source code
try:
    version = re.findall(r"^__version__ = '([^']+)'\r?$",
                         txt, re.M)[0]
except IndexError:
    raise RuntimeError('Unable to determine version.')



# function to read requirements, and include them as package dependencies
def get_requirements(*files):
    # read requirements.txt file and return them as a list of strings
    reqs = []
    for file in files:
        with open(file) as f:
            req = f.readlines()
        reqs += [r.strip() for r in req]
    return reqs  # clean lines from blank spaces and line breaks


setup(
    name='crepobenchmark',
    version=version,
    description='Benchmark for inference of credal networks.',
    long_description='Benchmark for inference of credal networks.',
    #long_description_content_type="text/markdown",
    author='Rafael Cabañas, Alessandro Antonucci',
    author_email="rcabanas@idsia.ch, alessandro@antonucci.ch",
    url='',
    #download_url='https://github.com/.../archive/{}.tar.gz'.format(version),
    keywords='credal networks',
    license='GNU GPLv3',
    classifiers=['Intended Audience :: Developers',
                 'Intended Audience :: Education',
                 'Intended Audience :: Science/Research',
                 'License :: OSI Approved :: Apache Software License',
                 'Operating System :: POSIX :: Linux',
                 'Operating System :: MacOS :: MacOS X',
                 'Operating System :: Microsoft :: Windows',
                 'Programming Language :: Python :: 3.4'],
    packages=['crepobenchmark'],
    python_requires='>=3.5',
    install_requires=['pandas~=1.1.0'],
    # extras_require={
    #     'gpu': get_requirements('requirements/gpu.txt'),
    #     'visualization': get_requirements('requirements/visualization.txt'),
    #     'datasets': get_requirements('requirements/datasets.txt'),
    #     'all': get_requirements('requirements/visualization.txt', 'requirements/datasets.txt'),
    #     'all-gpu': get_requirements('requirements/gpu.txt', 'requirements/visualization.txt', 'requirements/datasets.txt'),
    #
    # },
    # tests_require=get_requirements('requirements/test.txt'),
    include_package_data=True,
)