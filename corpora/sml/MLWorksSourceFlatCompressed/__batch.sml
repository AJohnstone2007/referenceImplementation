require "__toplevel";
require "__mlworks_io";
require "__encapsulate";
require "__version";
require "__license";
require "__proj_file";
require "__user_options";
require "../system/__mlworks_exit";
require "_batch";
require "../basis/__general";
structure Batch_ = Batch(
structure Io = MLWorksIo_
structure Encapsulate = Encapsulate_
structure User_Options = UserOptions_
structure TopLevel = TopLevel_
structure ProjFile = ProjFile_
structure Version = Version_
structure License = License_
structure Exit = MLWorksExit);
MLWorks.Internal.Runtime.modules := [];
val _ = MLWorksExit.exit (Batch_.obey (MLWorks.arguments ()));
