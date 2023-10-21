require "__user_context";
require "__shell_types";
require "__shell_utils";
require "__incremental";
require "../system/__getenv";
require "../system/__os";
require "../system/__mlworks_exit";
require "../basis/__list";
require "../main/__version";
require "../main/__license";
require "../main/__user_options";
require "../main/__preferences";
require "../main/__mlworks_io";
require "../main/__proj_file";
require "../main/__info";
require "_save_image";
structure SaveImage_ =
SaveImage (
structure UserContext = UserContext_
structure ShellTypes = ShellTypes_
structure ShellUtils = ShellUtils_
structure Incremental = Incremental_
structure Getenv = Getenv_
structure OS = OS
structure Io = MLWorksIo_
structure Info = Info_
structure Exit = MLWorksExit
structure Version = Version_
structure License = License_
structure UserOptions = UserOptions_
structure Preferences = Preferences_
structure List = List
structure ProjFile = ProjFile_
);
