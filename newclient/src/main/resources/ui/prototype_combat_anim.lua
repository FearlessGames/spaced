RegisterEvent("UNEQUIPPED_ITEM", function(event, entity, item, containerType)
		if item:GetName() == "s-11" or item:GetName() == "Disc-4" then
			SetPlaneMode(false)
		end	
end)

RegisterEvent("EQUIPPED_ITEM", function(event, entity, item, containerType)
	if IsSelf(entity) then
	StanceLowStop()	
	LungeRightStop()
	StanceRighthandStop()
--	FireLeftStop()	
	ShootLeftStop()	

	AimRifleStop()
	FireRifleStop()
	SwingRifleStop()
	StrikeRifleStop()	
	StanceRifleStop()

	StanceRighthandStop()
	StanceLowStop()	
	
	
	
		if item:GetName() == "Mk1 Assault Rifle" then
			combatAnims = 1
		end		
		if item:GetName() == "e-Blade" then
			combatAnims = 2
		end		
		if item:GetName() == "Lazor Pistol" then
			combatAnims = 1
		end	
		if item:GetName() == "s-11" or item:GetName() == "Disc-4" then
			SetPlaneMode(true)
		end	
		
	end
end)


function stopCastAnimation(event, attacker, spell)
	if IsSelf(attacker) then
		local name = spell:GetName()
		if name == "Lazor blast" then
--			ShootLeftStop()
			JumpKickSlashStop()
			StanceLowStop()	
			AimRifleStop()	
			FireRifleStop()				
		end	
		if name == "Melee" then
			StanceRighthandStop()
			StanceLowStop()		
			StrikeRifleStop()			
		end			

	end
end
RegisterEvent("SPELLCAST_STOPPED",  stopCastAnimation)


RegisterEvent("SPELLCAST_STARTED", function(event, attacker, target, spell)
	if IsSelf(attacker) then
		local name = spell:GetName()
		if name == "Lazor blast" and combatAnims == 2 then --sword
			JumpKickSlash()
			StanceRifleStop()			
			LungeRightStop()	
			StanceLowStop()				
		end	
		if name == "Melee" and combatAnims == 2 then
			StanceRighthand()
			StanceRifleStop()		
			LungeRightStop()			
			StanceLowStop()			
		end	
		
		if name == "Lazor blast" and combatAnims == 1 then --rifle/pistol
			AimRifle()
			
			LungeRightStop()	
			StanceLowStop()				
		end	
		if name == "Melee" and combatAnims == 1 then
			StrikeRifle()	
			
			LungeRightStop()
			StanceLowStop()			
		end		
	end	
end)


function completeCastAnimation(event, attacker, spell)
	if IsSelf(attacker) then
		local name = spell:GetName()
		
		if name == "Lazor blast" and combatAnims == 2 then --sword
			JumpKickSlashStop()
--			JumpKickSlash()
--			StanceLowStop()				
		end				
		if name == "Melee" and combatAnims == 2 then

			StanceLowStop()	

		end			

		if name == "Lazor blast" and combatAnims == 1 then --rifle
			FireRifle()
			
			AimRifleStop()			
			StanceLowStop()				
		end				
		if name == "Melee" and combatAnims == 1 then
		
			StanceLowStop()				
		end			
		
	end
end
RegisterEvent("SPELLCAST_COMPLETED",  completeCastAnimation)



RegisterEvent("UNIT_COMBAT", function(event, attacker, target, actionType, damage, school)
    if(actionType == "WOUND") then
	
		
    	if IsSelf(attacker) and combatAnims == 2 then --sword
 		FireRifleStop()	
		AimRifleStop()   	
		LungeRightStop()
		StanceRighthandStop()	
		StrikeRifleStop()	
		
		
    	end
		
	  --  if IsSelf(target) and combatAnims == 2 then
	--		StanceLow()
    --	end	
		
		if IsSelf(attacker) and combatAnims == 1 then --rifle
 		FireRifleStop()	
		AimRifleStop()   	
--		FireLeftStop()	
		LungeRightStop()
		StanceRighthandStop()	
		StrikeRifleStop()						
		StanceRifle()
						
		end		
		
		if IsSelf(target) and combatAnims == 1 then
			StanceRifle()						
		end		
		
	end
end)



RegisterEvent("UNEQUIPPED_ITEM", function(event, entity, item, containerType)
	if IsSelf(entity) then
--		playerInventory:AddItem(item)
	end
end)







RegisterEvent("PLAYER_LEFT_COMBAT", function(event)
	StanceLowStop()	
	LungeRightStop()
	StanceRighthandStop()
--	FireLeftStop()	
--	ShootLeftStop()	

	AimRifleStop()
	FireRifleStop()
	SwingRifleStop()
	StrikeRifleStop()	
	StanceRifleStop()
end)
