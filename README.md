# CREPO: An Open Repository to Benchmark Credal Network Algorithms
The Python package for CREPO can be installed as follows.


```python
!pip install crepobenchmark
```

Import the package and then repository is loaded.


```python
import crepobenchmark as crb
crb.download_metadata()
```

Access to the benchmark data:


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
      <td>vs_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>sing</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-sing_n4_mID2_mD6_mV4_...</td>
      <td>3</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.476609993 0.614758876376 0.385241123624 0.52...</td>
      <td>47.0</td>
    </tr>
    <tr>
      <th>1</th>
      <td>vs_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>sing</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-sing_n4_mID2_mD6_mV4_...</td>
      <td>0</td>
      <td>3</td>
      <td>0</td>
      <td>0.21158887521400385 0.31777107907580027 0.4676...</td>
      <td>65.0</td>
    </tr>
    <tr>
      <th>2</th>
      <td>vm_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>mult</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-mult_n4_mID2_mD6_mV4_...</td>
      <td>2</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.142387054038 0.529712535815 0.10009022261400...</td>
      <td>310312.0</td>
    </tr>
    <tr>
      <th>3</th>
      <td>vm_n4_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>mult</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-mult_n4_mID2_mD6_mV4_...</td>
      <td>0</td>
      <td>2</td>
      <td>0</td>
      <td>0.012342690023588145 0.5399930948722422 0.0 0....</td>
      <td>391599.0</td>
    </tr>
    <tr>
      <th>4</th>
      <td>vs_n5_mID2_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>5</td>
      <td>cve</td>
      <td>sing</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-sing_n5_mID2_mD6_mV4_...</td>
      <td>4</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.139525240230232 0.40904841081014 0.233851725...</td>
      <td>133.0</td>
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
      <th>83</th>
      <td>vm_n10_mID6_mD6_mV4_nV2-1</td>
      <td>2</td>
      <td>6</td>
      <td>6</td>
      <td>4</td>
      <td>10</td>
      <td>cve</td>
      <td>mult</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-mult_n10_mID6_mD6_mV4...</td>
      <td>3</td>
      <td>8 6 0</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>84</th>
      <td>vs_n4_mID2_mD6_mV4_nV4-1</td>
      <td>4</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>sing</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-sing_n4_mID2_mD6_mV4_...</td>
      <td>3</td>
      <td>NaN</td>
      <td>0</td>
      <td>0.020474433986000004 0.5962191198079999 0.4037...</td>
      <td>173.0</td>
    </tr>
    <tr>
      <th>85</th>
      <td>vs_n4_mID2_mD6_mV4_nV4-1</td>
      <td>4</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>sing</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-sing_n4_mID2_mD6_mV4_...</td>
      <td>0</td>
      <td>3</td>
      <td>0</td>
      <td>0.010658368965991724 0.9162321172536735 0.0026...</td>
      <td>241.0</td>
    </tr>
    <tr>
      <th>86</th>
      <td>vm_n4_mID2_mD6_mV4_nV4-1</td>
      <td>4</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>mult</td>
      <td>marg</td>
      <td>./networks/vmodel/vmodel-mult_n4_mID2_mD6_mV4_...</td>
      <td>2</td>
      <td>NaN</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>87</th>
      <td>vm_n4_mID2_mD6_mV4_nV4-1</td>
      <td>4</td>
      <td>6</td>
      <td>2</td>
      <td>4</td>
      <td>4</td>
      <td>cve</td>
      <td>mult</td>
      <td>cond</td>
      <td>./networks/vmodel/vmodel-mult_n4_mID2_mD6_mV4_...</td>
      <td>0</td>
      <td>2</td>
      <td>0</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
  </tbody>
</table>
<p>88 rows × 15 columns</p>
</div>



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
!ls -l | grep model
```

    -rw-r--r-- 1 root root  449 Feb 19 17:13 model.uai


Finally, an inference task can be done by invoking Crema:


```python
crb.run_crema("model.uai", target=0, method="cve")
```




    {'arg_str': '-t;--method=cve;-r;1;-w;0;-x;0;-y;1;--timeout=600;model.uai',
     'err_msg': '',
     'interval_result': [0.0026153357047309983,
      0.7449456975772765,
      0.060534723389944506,
      0.982702139276319,
      0.0008971229475704658,
      0.9140697811019305,
      0.002723271185938287,
      0.18491484184914841],
     'time': 45.0}


