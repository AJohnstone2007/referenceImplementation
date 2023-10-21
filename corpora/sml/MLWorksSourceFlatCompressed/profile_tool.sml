signature PROFILE_TOOL =
sig
type ToolData
type user_context
type Widget
type user_preferences
val create : Widget * user_preferences *
(unit -> ToolData) *
(unit -> user_context) -> MLWorks.Profile.profile -> unit
end;
