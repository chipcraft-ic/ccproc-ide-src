Why EmbSysRegView package is included in this repository
--------------------------------------------------------
EmbSysRegView plugin was downloaded from:
https://sourceforge.net/projects/embsysregview/files/embsysregview/
It is included in this repository because its update site
(http://embsysregview.sourceforge.net/update) does not work properly
in modern Eclipse.

EmbSysRegView Data plugin changes
---------------------------------
Data plugin has been stripped of all microcontrollers and instead SVD for
CCPROC was put into data/CCPROC/ChipCraft/ccproc.xml. You can get this file
from ccsdk/svd directory.
