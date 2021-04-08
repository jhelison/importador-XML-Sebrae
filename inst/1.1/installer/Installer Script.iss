; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Importador de XML SEBRAE"
#define MyAppVersion "1.1"
#define MyAppExeName "Importador de XML SEBRAE.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{222DCB64-D7B6-496E-B8F0-234A3AFC0C54}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
DefaultDirName={autopf}\{#MyAppName}
DisableProgramGroupPage=yes
; The [Icons] "quicklaunchicon" entry uses {userappdata} but its [Tasks] entry has a proper IsAdminInstallMode Check.
UsedUserAreasWarning=no
; Uncomment the following line to run in non administrative install mode (install for current user only.)
;PrivilegesRequired=lowest
OutputDir=C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\installer
OutputBaseFilename=Importador de XML SEBRAE
SetupIconFile=C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\Wefunction-Woofunction-Database-add (1).ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "brazilianportuguese"; MessagesFile: "compiler:Languages\BrazilianPortuguese.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 6.1; Check: not IsAdminInstallMode

[Files]
Source: "C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\Importador de XML SEBRAE.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\Importador de XML SEBRAE.manifest"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\SmartCard.cfg"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\Token.cfg"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\tray.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Jhelison\eclipse-workspace\Final_project\inst\1.1\exe\Wefunction-Woofunction-Database-add (1).ico"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon

