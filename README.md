# CREPO: An Open Repository to Benchmark Credal Network Algorithms

## Setup
The latest version of the Python package for CREPO can be installed from PyPI as follows.


```python
!pip install crepobenchmark
```

or from github source code:


```python
!pip install git+https://github.com/IDSIA-papers/crepo.git@main#"subdirectory=src/python"
```

Import the package.


```python
import crepobenchmark as crb
```

## Benchmarking data
The following function will plot a table summarizing the features of the benchmarking dataset.


```python
crb.describe()
```





<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>property</th>
      <th>value</th>
      <th>description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>num_vert</td>
      <td>[2, 4, 6]</td>
      <td>Number of vertices in the credal sets. It is a...</td>
    </tr>
    <tr>
      <th>1</th>
      <td>max_degree</td>
      <td>[6]</td>
      <td>Maximum arc degree in the model.</td>
    </tr>
    <tr>
      <th>2</th>
      <td>max_indegree</td>
      <td>[2, 4, 6]</td>
      <td>Maximum arc indegree in the model.</td>
    </tr>
    <tr>
      <th>3</th>
      <td>max_values</td>
      <td>[4]</td>
      <td>Maximum cardinality plus 1</td>
    </tr>
    <tr>
      <th>4</th>
      <td>nodes</td>
      <td>[4, 5, 6, 7, 8, 9, 10]</td>
      <td>Number of nodes in the model.</td>
    </tr>
    <tr>
      <th>5</th>
      <td>method</td>
      <td>[approxlp, cve, cve_ch10, cve_ch5]</td>
      <td>Inference method.</td>
    </tr>
    <tr>
      <th>6</th>
      <td>kind</td>
      <td>[sing, mult]</td>
      <td>Topology of the DAG: singly or multy connected.</td>
    </tr>
    <tr>
      <th>7</th>
      <td>query_type</td>
      <td>[marg, cond]</td>
      <td>Marginal or conditional query.</td>
    </tr>
    <tr>
      <th>8</th>
      <td>Vmodels</td>
      <td>378</td>
      <td>Count of models with a vertex specification.</td>
    </tr>
    <tr>
      <th>9</th>
      <td>Hmodels</td>
      <td>378</td>
      <td>Count of models with a linear constraints spec...</td>
    </tr>
    <tr>
      <th>10</th>
      <td>rows</td>
      <td>1920</td>
      <td>Number of rows in the current benchmarking data.</td>
    </tr>
    <tr>
      <th>11</th>
      <td>columns</td>
      <td>15</td>
      <td>Number of rows in the current benchmarking data.</td>
    </tr>
  </tbody>
</table>
</div>



The data itself can be obtained as follows.


```python
data = crb.get_benchmark_data()
data
```





<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>label</th>
      <th>num_vert</th>
      <th>max_degree</th>
      <th>max_indegree</th>
      <th>max_values</th>
      <th>nodes</th>
      <th>method</th>
      <th>kind</th>
      <th>query_type</th>
      <th>filename</th>
      <th>target</th>
      <th>observed</th>
      <th>barren</th>
      <th>interval_result</th>
      <th>time</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>hs_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>approxlp</td>
      <td>sing</td>
      <td>marg</td>
      <td>./networks/hmodel/hmodel-sing_n4_mID2_mD6_mV4_...</td>
      <td>3</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.47661183887412617 0.6147556334817609 0.38524...</td>
      <td>477.0</td>
    </tr>
    <tr>
      <th>1</th>
      <td>hs_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>approxlp</td>
      <td>sing</td>
      <td>cond</td>
      <td>./networks/hmodel/hmodel-sing_n4_mID2_mD6_mV4_...</td>
      <td>0</td>
      <td>3</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>2</th>
      <td>hm_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>approxlp</td>
      <td>mult</td>
      <td>marg</td>
      <td>./networks/hmodel/hmodel-mult_n4_mID2_mD6_mV4_...</td>
      <td>2</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.14280081709746253 0.524457784601085 0.100093...</td>
      <td>1277.0</td>
    </tr>
    <tr>
      <th>3</th>
      <td>hm_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>approxlp</td>
      <td>mult</td>
      <td>cond</td>
      <td>./networks/hmodel/hmodel-mult_n4_mID2_mD6_mV4_...</td>
      <td>0</td>
      <td>2</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>4</th>
      <td>hs_n5_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>5</td>
      <td>approxlp</td>
      <td>sing</td>
      <td>marg</td>
      <td>./networks/hmodel/hmodel-sing_n5_mID2_mD6_mV4_...</td>
      <td>4</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.13952568805044846 0.40904652506095185 0.2338...</td>
      <td>1037.0</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>1915</th>
      <td>vm_n5_mID4_mD6_mV4_nV6-1</td>
      <td>6</td>
      <td>6</td>
      <td>4</td>
      <td>4</td>
      <td>5</td>
      <td>cve_ch10</td>
      <td>mult</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-mult_n5_mID4_mD6_mV4_...</td>
      <td>0</td>
      <td>4</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>1916</th>
      <td>vs_n5_mID4_mD6_mV4_nV6-1</td>
      <td>6</td>
      <td>6</td>
      <td>4</td>
      <td>4</td>
      <td>5</td>
      <td>cve_ch5</td>
      <td>sing</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-sing_n5_mID4_mD6_mV4_...</td>
      <td>4</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.02162501899836531 0.6233117954158697 0.04147...</td>
      <td>140053.0</td>
    </tr>
    <tr>
      <th>1917</th>
      <td>vs_n5_mID4_mD6_mV4_nV6-1</td>
      <td>6</td>
      <td>6</td>
      <td>4</td>
      <td>4</td>
      <td>5</td>
      <td>cve_ch5</td>
      <td>sing</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-sing_n5_mID4_mD6_mV4_...</td>
      <td>0</td>
      <td>4</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>1918</th>
      <td>vm_n5_mID4_mD6_mV4_nV6-1</td>
      <td>6</td>
      <td>6</td>
      <td>4</td>
      <td>4</td>
      <td>5</td>
      <td>cve_ch5</td>
      <td>mult</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-mult_n5_mID4_mD6_mV4_...</td>
      <td>4</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.19665011233872 0.934857703437616 0.065142296...</td>
      <td>139.0</td>
    </tr>
    <tr>
      <th>1919</th>
      <td>vm_n5_mID4_mD6_mV4_nV6-1</td>
      <td>6</td>
      <td>6</td>
      <td>4</td>
      <td>4</td>
      <td>5</td>
      <td>cve_ch5</td>
      <td>mult</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-mult_n5_mID4_mD6_mV4_...</td>
      <td>0</td>
      <td>4</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
  </tbody>
</table>
<p>1920 rows × 15 columns</p>
</div>



## Credal networks specificaitons
The specification of any of the models in UAI format can be obtained as follows.


```python
modelname = "vs_n4_mID2_mD6_mV4_nV2-1"
print(crb.get_model(modelname))
```

    V-CREDAL
    4
    4 4 2 2
    4
    1 0
    2 0 1
    2 1 2
    2 2 3
    
    8
    0.218 0.48 0.254 0.048
    0.296 0.603 0.086 0.015
    
    8
    0.241 0.617 0.119 0.023
    0.004 0.989 0.003 0.004
    8
    0.197 0.711 0.048 0.044
    0.039 0.948 0.006 0.007
    8
    0.919 0.004 0.009 0.068
    0.002 0.017 0.001 0.98
    8
    0.049 0.108 0.13 0.713
    0.095 0.428 0.427 0.05
    
    4
    0.423 0.577
    0.752 0.248
    4
    0.616 0.384
    0.667 0.333
    4
    0.875 0.125
    0.087 0.913
    4
    0.007 0.993
    0.517 0.483
    
    4
    0.674 0.326
    0.677 0.323
    4
    0.361 0.639
    0.464 0.536
    


This specification can also be saved into a file:


```python
crb.save_model(modelname, "model.uai")
!ls | grep model
```

    model.uai


## Running inference with CREMA

Finally, an inference task can be done by invoking Crema:


```python
crb.run_crema("model.uai", target=0, method="cve")
```

    -t --method=cve -r 1 -w 0 -x 0 -y 1 --timeout=600 model.uai





    {'time': 55.0,
     'interval_result': [0.0026153357047309983,
      0.7449456975772765,
      0.060534723389944506,
      0.982702139276319,
      0.0008971229475704658,
      0.9140697811019305,
      0.002723271185938287,
      0.18491484184914841],
     'err_msg': '',
     'arg_str': '-t;--method=cve;-r;1;-w;0;-x;0;-y;1;--timeout=600;model.uai'}


