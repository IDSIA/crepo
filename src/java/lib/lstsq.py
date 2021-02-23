import numpy as np
import sys



def get_constraints(P):

    A = np.hstack([P[:,:-1], np.ones((P.shape[0],1))])
    b = P[:,-1].flatten()
    #(A,b)
    alpha, _ , _, _ = np.linalg.lstsq(A, b, rcond=None)

    coeff = [*[round(-x,5) for x in alpha[:-1]], 1.0]
    vals = round(alpha[-1],18)
    return coeff, vals



#try:
P = np.reshape([float(d) for d in  sys.argv[1:]], (2, (len(sys.argv)-1)//2))
coeff, vals = get_constraints(P)
print("\t".join([str(d) for d in [vals, *[-x for x in coeff]]]))
#except:
#exit(-1)

