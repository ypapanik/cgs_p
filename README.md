Improved Gibbs Sampling Parameter Estimators for Latent Dirichlet Allocation - code

This code is mainly meant to be used for easy replication of our experiments.
All experiments are in the package gr.auth.csd.mlkd.experiments and for running it is normally sufficient to run as:
experiment_class -trainingFile trainFile -testFile testFile

We have included also two simple classes to show how the output of a trained WarpLDA model or Sparse LDA model (MALLET) can be used to compute the estimators that we propose, namely CGS_pWithMALLET and CGS_pWithWarpLDA. 

In case you would like to cite the methods or this code, use:

@article{JMLR:v18:16-526,
  author  = {Yannis Papanikolaou and James R. Foulds and Timothy N. Rubin and Grigorios Tsoumakas},
  title   = {Dense Distributions from Sparse Samples: Improved Gibbs Sampling Parameter Estimators for LDA},
  journal = {Journal of Machine Learning Research},
  year    = {2017},
  volume  = {18},
  number  = {62},
  pages   = {1-58},
  url     = {http://jmlr.org/papers/v18/16-526.html}
}

For any question, please contact Yannis Papanikolaou at ypapanik@csd.auth.gr
