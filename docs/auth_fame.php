<?php
/**
* Apache auth plug-in for phpBB3
*
* Authentication plug-ins is largely down to Sergey Kanareykin, our thanks to him.
*
* @package login
* @version $Id$
* @copyright (c) 2005 phpBB Group
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*
*/

/**
* @ignore
*/
if (!defined('IN_PHPBB'))
{
	exit;
}

function passwordHash($username, $password, $salt)
{
	return hash("sha512", $username . $password . $salt);
}

/**
* Checks whether the user is identified to fame
* Only allow changing authentication to apache if the user is identified
* Called in acp_board while setting authentication plugins
*
* @return boolean|string false if the user is identified and else an error message
*/
function init_fame()
{
	global $config;
	$config['fame_location'] = $config['fame_location'];
	return false;
}

/**
* Login function
*/
function login_fame(&$username, &$password)
{
	global $db;

	// do not allow empty password
	if (!$password)
	{
		return array(
			'status'	=> LOGIN_ERROR_PASSWORD,
			'error_msg'	=> 'NO_PASSWORD_SUPPLIED',
			'user_row'	=> array('user_id' => ANONYMOUS),
		);
	}

	if (!$username)
	{
		return array(
			'status'	=> LOGIN_ERROR_USERNAME,
			'error_msg'	=> 'LOGIN_ERROR_USERNAME',
			'user_row'	=> array('user_id' => ANONYMOUS),
		);
	}
	error_log("Requesting salt for user " . $username);	
	
	$saltsResponse = http_parse_message(http_get("http://fame.fearlessgames.se/api/auth/requestSalts.html?userName=".$username, array("httpauth"=>"remote:remote", "httpauthtype"=>HTTP_AUTH_BASIC), $info));
	if($saltsResponse->responseCode != 200)
	{
		error_log("Failed to request salts response code: " .$saltsResponse->responseCode);
		return array(
		'status'		=> LOGIN_ERROR_EXTERNAL_AUTH,
		'error_msg'		=> 'LOGIN_ERROR_USERNAME',
		'user_row'		=> array('user_id' => ANONYMOUS),
		);
	
	}
	$saltsData = explode(":", $saltsResponse->body);
	
	$userUniqueSalt = $saltsData[0];
	$oneTimeSalt = $saltsData[1];
	error_log($userUniqueSalt);
	error_log($oneTimeSalt);
	$salt = "beefcake";

	$fameBCrypt = crypt($username . $password, $userUniqueSalt);
	//$fameBCrypt = crypt($username . $password, '$2a$10$' . $userUniqueSalt);
	$fameHash = hash("sha512", $fameBCrypt . $oneTimeSalt . $salt);

	$requestData = array(	'userName' 	=> $username,
							'hash'		=> $fameHash,
							'serviceKey'=> "gonzales");
	$parameters = http_build_query($requestData);
	$getString = "http://fame.fearlessgames.se/api/auth/forum.html?" . $parameters;
	//error_log("Requesting uuid $getString");
	
	
	$uuidResponse = http_parse_message(http_get($getString, array("httpauth"=>"remote:remote", "httpauthtype"=>HTTP_AUTH_BASIC), $info));
	
	$idResponseData = explode(":", $uuidResponse->body);
	$userId = intval($idResponseData[0]);
	$userEmail = $idResponseData[1];
	$uuidStatus = $uuidResponse->responseCode;
	if($uuidStatus != 200)
	{
		error_log("Failed to auth against fame");
		return array(
		'status'		=> LOGIN_ERROR_EXTERNAL_AUTH,
		'error_msg'		=> 'LOGIN_ERROR_USERNAME',
		'user_row'		=> array('user_id' => ANONYMOUS),
	);
	
	}
	
	//error_log("Got uuid $userId and email $userEmail");
	
	//error_log("Looking up user in phpBB db");
	$sql = 'SELECT user_id, username, user_password, user_passchg, user_pass_convert, user_email, user_type, user_login_attempts
		FROM ' . USERS_TABLE . "
		WHERE username_clean = '" . $db->sql_escape(utf8_clean_string($username)) . "'";

	$result = $db->sql_query($sql);
	$row = $db->sql_fetchrow($result);
	$db->sql_freeresult($result);
	
	if ( $userId ) 
	{
		if ($row)
		{
			// User inactive...
			if ($row['user_type'] == USER_INACTIVE || $row['user_type'] == USER_IGNORE)
			{
				return array(
					'status'		=> LOGIN_ERROR_ACTIVE,
					'error_msg'		=> 'ACTIVE_ERROR',
					'user_row'		=> $row,
				);
			}
			// Successful login...
			return array(
			'status'		=> LOGIN_SUCCESS,
			'error_msg'		=> false,
			'user_row'		=> $row,);
		}

		// this is the user's first login so create an empty profile
		return array(
			'status'		=> LOGIN_SUCCESS_CREATE_PROFILE,
			'error_msg'		=> false,
			'user_row'		=> user_row_fame($userId, $username, $password, $salt, $userEmail),
		);
	}
	// Not logged into fame
	return array(
		'status'		=> LOGIN_ERROR_EXTERNAL_AUTH,
		'error_msg'		=> 'LOGIN_ERROR_EXTERNAL_AUTH_APACHE',
		'user_row'		=> array('user_id' => ANONYMOUS),
	);
}

/**
* Autologin function
*
* @return array containing the user row or empty if no auto login should take place
*/
/*function autologin_fame()
{
	global $db;

	if (!isset($_SERVER['PHP_AUTH_USER']))
	{
		return array();
	}

	$php_auth_user = $_SERVER['PHP_AUTH_USER'];
	$php_auth_pw = $_SERVER['PHP_AUTH_PW'];

	if (!empty($php_auth_user) && !empty($php_auth_pw))
	{
		set_var($php_auth_user, $php_auth_user, 'string', true);
		set_var($php_auth_pw, $php_auth_pw, 'string', true);

		$sql = 'SELECT *
			FROM ' . USERS_TABLE . "
			WHERE username = '" . $db->sql_escape($php_auth_user) . "'";
		$result = $db->sql_query($sql);
		$row = $db->sql_fetchrow($result);
		$db->sql_freeresult($result);

		if ($row)
		{
			return ($row['user_type'] == USER_INACTIVE || $row['user_type'] == USER_IGNORE) ? array() : $row;
		}

		if (!function_exists('user_add'))
		{
			global $phpbb_root_path, $phpEx;

			include($phpbb_root_path . 'includes/functions_user.' . $phpEx);
		}

		// create the user if he does not exist yet
		user_add(user_row_fame($php_auth_user, $php_auth_pw));

		$sql = 'SELECT *
			FROM ' . USERS_TABLE . "
			WHERE username_clean = '" . $db->sql_escape(utf8_clean_string($php_auth_user)) . "'";
		$result = $db->sql_query($sql);
		$row = $db->sql_fetchrow($result);
		$db->sql_freeresult($result);

		if ($row)
		{
			return $row;
		}
	}

	return array();
}*/

/**
* This function generates an array which can be passed to the user_add function in order to create a user
*/
function user_row_fame($userId, $username, $password, $salt, $userEmail)
{
	global $db, $config, $user;
	// first retrieve default group id
	$sql = 'SELECT group_id
		FROM ' . GROUPS_TABLE . "
		WHERE group_name = '" . $db->sql_escape('REGISTERED') . "'
			AND group_type = " . GROUP_SPECIAL;
	$result = $db->sql_query($sql);
	$row = $db->sql_fetchrow($result);
	$db->sql_freeresult($result);

	if (!$row)
	{
		trigger_error('NO_GROUP');
	}

	// generate user account data
	return array(
		'user_id'		=> $userId,
		'username'		=> $username,
		'user_password'	=> '', // passwordHash($username, $password, $salt),
		'user_email'	=> $userEmail,
		'group_id'		=> (int) $row['group_id'],
		'user_type'		=> USER_NORMAL,
		'user_ip'		=> $user->ip,
		'user_new'		=> ($config['new_member_post_limit']) ? 1 : 0,
	);
}

/**
* The session validation function checks whether the user is still logged in
*
* @return boolean true if the given user is authenticated or false if the session should be closed
*/
/*function validate_session_fame(&$user)
{
	// Check if PHP_AUTH_USER is set and handle this case
	if (isset($_SERVER['PHP_AUTH_USER']))
	{
		$php_auth_user = '';
		set_var($php_auth_user, $_SERVER['PHP_AUTH_USER'], 'string', true);

		return ($php_auth_user === $user['username']) ? true : false;
	}

	// PHP_AUTH_USER is not set. A valid session is now determined by the user type (anonymous/bot or not)
	if ($user['user_type'] == USER_IGNORE)
	{
		return true;
	}

	return false;
}
*/
?>


