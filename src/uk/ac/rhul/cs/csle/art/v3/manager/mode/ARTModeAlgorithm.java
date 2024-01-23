package uk.ac.rhul.cs.csle.art.v3.manager.mode;

public enum ARTModeAlgorithm {
  gllGeneratorPool, gllClusteredGeneratorPool,

  gllTWEGeneratorPool,

  mgllGeneratorPool,

  earley2007LinkedAPI,

  earleyLinkedAPI, earleyIndexedAPI, earleyIndexedPool, earleyIndexedData,

  earleyTableLinkedAPI, earleyTableIndexedAPI, earleyTableIndexedPool, earleyTableIndexedData,

  cnpLinkedAPI, cnpIndexedAPI, cnpIndexedPool, cnpGeneratorPool,

  lcnpLinkedAPI, lcnpIndexedAPI, lcnpIndexedPool, lcnpGeneratorPool,

  mcnpLinkedAPI, mcnpIndexedAPI, mcnpIndexedPool, mcnpGeneratorPool,

  lexerData, lexDFA,

  osbrdGenerator,

  grammarWrite,

  generateDepth, generateBreadth, generateRandom,
}
