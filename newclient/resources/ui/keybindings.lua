-- The keybindings
require("ui/chatmodel")
require("ui/chat")

OnKeyDown("RETURN", EnterKeyDown)
OnKeyDown("PAGEUP_PRIOR", PageUpKeyDown)
OnKeyDown("PAGEDOWN_NEXT", PageDownKeyDown)

OnKeyDown("W", MoveForward)
OnKeyUp("W", MoveForwardStop)
OnKeyDown("A", MoveLeft)
OnKeyUp("A", MoveLeftStop)
OnKeyDown("S", MoveBackwards)
OnKeyUp("S", MoveBackwardsStop)
OnKeyDown("D", MoveRight)
OnKeyUp("D", MoveRightStop)

OnKeyDown("E", LungeRight)  -- Combat Animation Prototype (E, Q, R, F, V)
OnKeyUp("E", LungeRightStop)
OnKeyDown("Q", AimRifle)

OnKeyUp("Q", AimRifleStop)
OnKeyDown("R", StanceRighthand)
OnKeyUp("R", StanceRighthandStop)
OnKeyDown("F", StanceLow)
OnKeyUp("F", StanceLowStop)
OnKeyDown("V", FireLeft)
OnKeyUp("V", FireLeftStop)

OnKeyDown("LSHIFT", SprintStart);
OnKeyUp("LSHIFT", SprintStop);

OnKeyDown("SPACE", StartJump)
OnKeyUp("SPACE", StopJump)

OnKeyDown("TAB", TabTarget)
