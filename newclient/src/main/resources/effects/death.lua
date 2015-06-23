local sparks = CreateParticleEffect("sparks")
local hardsmoke = CreateParticleEffect("hardsmoke")
local bigshockwave = CreateParticleEffect("bigshockwave")

sparks:playAtEntity("TARGET")
sparks:envelope(0, 2, 5)	
hardsmoke:playAtEntity("TARGET")
hardsmoke:envelope(0, 2, 8)
bigshockwave:playAtEntity("TARGET")
bigshockwave:envelope(0, 2, 5)

local sound = CreateSoundEffect("lowexplosion")
sound:playAtEntity("TARGET")
local mid = CreateSoundEffect("midexplosion")
mid:playAtEntity("TARGET")

local hi = CreateSoundEffect("hiexplosion")
hi:playAtEntity("TARGET")



return sparks, hardsmoke, bigshockwave, sound, mid, hi
