require "../main/__code_module";
require "../rts/gen/__objectfile";
require "../utils/__lists";
require "__inter_envtypes";
require "_interload";
structure InterLoad_ =
InterLoad (
structure Code_Module = Code_Module_
structure Inter_EnvTypes = Inter_EnvTypes_
structure ObjectFile = ObjectFile_
structure Lists = Lists_
);
