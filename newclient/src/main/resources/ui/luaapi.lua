function printFunctions(t)
	for methodName, methodDebugData in pairs(t) do
		print("","",methodDebugData:getLuaDescription())
		local i = 1
		for i = 1, 1000 do
			local name, type, desc = methodDebugData:getParameter(i)
			if not name then
				break
			end
			print("","","",name, type, desc)
		end
	end
end

function printApi()
	for className, classData in pairs(getExposedClasses()) do
		print(className, classData.super or "no super", classData.hasDebug)
		local t = classData.methods
		if t and pairs(t)() then
			print("", "methods:")
			printFunctions(t)
		end
		local t = classData.functions
		if t and pairs(t)() then
			print("", "functions:")
			printFunctions(t)
		end
	end
end

