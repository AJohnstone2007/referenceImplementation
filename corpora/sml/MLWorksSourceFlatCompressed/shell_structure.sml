signature SHELL_STRUCTURE =
sig
type ShellData
type Context
val make_shell_structure : bool -> ShellData ref * Context -> Context
end;
