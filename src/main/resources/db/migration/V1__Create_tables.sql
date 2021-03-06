CREATE TABLE LOCAL_OGEL (
  ID   TEXT NOT NULL UNIQUE,
  NAME TEXT,
  CANLIST TEXT,
  CANTLIST TEXT,
  MUSTLIST TEXT,
  HOWTOUSELIST TEXT
);

CREATE TABLE LOCAL_CONTROL_CODE_CONDITIONS (
  OGEL_ID TEXT NOT NULL,
  CONTROL_CODE TEXT NOT NULL,
  CONDITION_DESC TEXT,
  CONDITION_DESC_CONTROL_CODES TEXT,
  ALLOWED BOOLEAN
);