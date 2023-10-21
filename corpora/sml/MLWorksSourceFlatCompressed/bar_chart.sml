signature BAR_CHART =
sig
type Widget
datatype bar = Bar of
{height: real,
key: string,
click_action: int -> unit}
datatype chart_spec = ChartSpec of
{maximum_bars : int,
bar_width : int,
maximum_tick_space : int,
ideal_label_space : int}
val make :
chart_spec *
(chart_spec -> unit) *
(unit -> (bar list * int)) *
Widget ->
{widget: Widget,
initialize : unit -> unit,
update: unit -> unit,
popup: unit-> unit}
end
;
