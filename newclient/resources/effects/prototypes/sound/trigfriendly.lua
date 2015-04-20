local effect = CreateSoundPrototype("/sound/sfx/Triggers/Friendly/FriendlyTrig1.wav")

effect:setFalloff(1.7)
effect:setGain(0.03 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(1.5)

return effect