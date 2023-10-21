(TextIO.closeOut TextIO.stdErr; "Fail")
handle IO.Io _ =>
(TextIO.closeOut TextIO.stdOut; "Fail")
handle IO.Io _ => "Pass"
;
