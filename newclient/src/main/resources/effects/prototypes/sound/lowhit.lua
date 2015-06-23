local effect = CreateSoundPrototype("/sound/sfx/events/hits/lowhit.wav")

effect:setFalloff(0.5)
effect:setGain(0.4 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(9.0)

return effect