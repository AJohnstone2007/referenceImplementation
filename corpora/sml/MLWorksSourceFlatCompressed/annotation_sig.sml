require "basic_types";
signature ANNOTATION =
sig
exception ANNOTATION of string
type widgetPackFun
type widgetAddFun
type widgetDelFun
type widgetUpdFun
type widgetAddFunc
type widgetDelFunc
val selTextWidWidId : BasicTypes.Widget -> BasicTypes.WidId
val selTextWidScrollType : BasicTypes.Widget -> BasicTypes.ScrollType
val selTextWidAnnoText : BasicTypes.Widget -> BasicTypes.AnnoText
val selTextWidText : BasicTypes.Widget -> string
val selTextWidAnnotations : BasicTypes.Widget -> BasicTypes.Annotation list
val selTextWidPack : BasicTypes.Widget -> BasicTypes.Pack list
val selTextWidConfigure : BasicTypes.Widget -> BasicTypes.Configure list
val selTextWidBinding : BasicTypes.Widget -> BasicTypes.Binding list
val updTextWidScrollType : BasicTypes.Widget -> BasicTypes.ScrollType ->
BasicTypes.Widget
val updTextWidAnnotations : BasicTypes.Widget -> BasicTypes.Annotation list ->
BasicTypes.Widget
val updTextWidPack : BasicTypes.Widget -> BasicTypes.Pack list ->
BasicTypes.Widget
val updTextWidConfigure : BasicTypes.Widget -> BasicTypes.Configure list ->
BasicTypes.Widget
val updTextWidBinding : BasicTypes.Widget -> BasicTypes.Binding list ->
BasicTypes.Widget
val getTextWidWidgets : BasicTypes.Widget -> BasicTypes.Widget list
val getTextWidAnnotationWidgetAssList :
BasicTypes.Widget ->
(BasicTypes.Annotation * BasicTypes.Widget list) list
val addTextWidWidget : (widgetAddFun) ->
BasicTypes.Widget -> BasicTypes.Widget ->
BasicTypes.WidPath -> BasicTypes.Widget
val deleteTextWidWidget : (widgetDelFun) ->
BasicTypes.Widget -> BasicTypes.WidId ->
BasicTypes.WidPath -> BasicTypes.Widget
val updTextWidWidget : (widgetUpdFun) ->
BasicTypes.Widget -> BasicTypes.WidId ->
BasicTypes.WidPath -> BasicTypes.Widget ->
BasicTypes.Widget
val selAnnotationType : BasicTypes.Annotation -> BasicTypes.AnnotationType
val selAnnotationId : BasicTypes.Annotation -> BasicTypes.AnnId
val selAnnotationConfigure : BasicTypes.Annotation -> BasicTypes.Configure list
val selAnnotationBinding : BasicTypes.Annotation -> BasicTypes.Binding list
val selAnnotationMarks : BasicTypes.Annotation ->
(BasicTypes.Mark * BasicTypes.Mark) list
val selAnnotationWidId : BasicTypes.Annotation -> BasicTypes.WidId
val selAnnotationWidgets : BasicTypes.Annotation -> BasicTypes.Widget list
val selAnnotationWidgetConfigure : BasicTypes.Annotation -> BasicTypes.Configure list
val updAnnotationConfigure : BasicTypes.Annotation -> BasicTypes.Configure list ->
BasicTypes.Annotation
val updAnnotationBinding : BasicTypes.Annotation -> BasicTypes.Binding list ->
BasicTypes.Annotation
val updAnnotationWidgets : BasicTypes.Annotation -> BasicTypes.Widget list ->
BasicTypes.Annotation
val get : BasicTypes.Widget -> BasicTypes.AnnId -> BasicTypes.Annotation
val getBindingByName
: BasicTypes.Widget -> BasicTypes.AnnId -> string ->
BasicTypes.Action
val upd : BasicTypes.Widget -> BasicTypes.AnnId -> BasicTypes.Annotation ->
BasicTypes.Widget
val add : widgetPackFun ->
BasicTypes.Widget -> BasicTypes.Annotation -> BasicTypes.Widget
val delete : widgetDelFunc ->
BasicTypes.Widget -> BasicTypes.AnnId -> BasicTypes.Widget
val addAnnotationConfigure : BasicTypes.Widget -> BasicTypes.AnnId ->
BasicTypes.Configure list -> BasicTypes.Widget
val addAnnotationBinding : BasicTypes.Widget -> BasicTypes.AnnId ->
BasicTypes.Binding list -> BasicTypes.Widget
val pack : widgetPackFun -> BasicTypes.TclPath -> BasicTypes.IntPath ->
BasicTypes.Annotation -> unit
val newId : unit -> BasicTypes.CItemId
val newFrId : unit -> BasicTypes.WidId
val readSelection : BasicTypes.Widget -> (BasicTypes.Mark * BasicTypes.Mark) list
val readMarks : BasicTypes.Widget -> BasicTypes.AnnId ->
(BasicTypes.Mark * BasicTypes.Mark) list
end
;
