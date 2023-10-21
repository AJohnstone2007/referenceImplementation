require "../editor/__custom";
require "../utils/__lists";
require "../utils/__crash";
require "../main/__preferences";
require "../basics/__location";
require "__win32";
require "_editor";
structure Editor_ = Editor(
structure Preferences = Preferences_
structure Location = Location_
structure CustomEditor = CustomEditor_
structure Lists = Lists_
structure Win32 = Win32_
structure Crash = Crash_
)
;
