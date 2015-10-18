if not gui_enabled() then return end

pluginName = "SSL Wireshark Plugin"
defaultPath = "~/.wireshark/plugins/SSL_Wireshark_Plugin/"

function launch_script_sslmanager()
	local scriptPath = defaultPath .. "SSLManager.jar"
	-- print(string.format("script_init path = %s", scriptPath))
   
	local statusCode = assert(os.execute(string.format("java -jar %s &", scriptPath)))
	displayMessage(statusCode, scriptPath)
end

function displayMessage(statusCode, name)
	if statusCode == 0 then
		print(string.format("Sucess with %s", name))
	else
		error(string.format("Error with %s", name))
	end
end

function create_about_window()
	year = "2015";
	version = "1.0"
   	cs_about_win = TextWindow.new("About ESGI Plugin SSL")
   	cs_about_win:append(string.format("ESGI Plugin SSL for WireShark (c) %s\n", year))
   	cs_about_win:append(string.format("Version %s\n", version))
   	cs_about_win:append("Developed by BEKAERT Duane, GROSGOJAT Kevin, SIN RONIA Christophe \n")
   	cs_about_win:append("\n")
   	cs_about_win:append("This Wireshark plugin allows you to launch a man in the middle attack with ARP poisoning method and trick the user with sslstrip to steal his login/password on websites without the SSL encryption.")
end

-- Add new menu items to WireShark under the Tools menu
register_menu(pluginName .. "/SSLManager", launch_script_sslmanager, MENU_TOOLS_UNSORTED)
register_menu(pluginName .. "/About", create_about_window, MENU_TOOLS_UNSORTED)

