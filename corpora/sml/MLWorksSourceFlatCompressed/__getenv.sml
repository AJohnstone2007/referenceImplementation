require "_win_ntgetenv";
require "__windows";
require "__os";
structure Getenv_ =
Win_ntGetenv (structure OSPath = OS.Path
structure Windows = Windows)
;
