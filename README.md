# BirthDayCelebrate
登録された誕生日にログインしたらお祝いメッセージが流れ，プレゼントのケーキが渡されます

#使い方
まず初めに，
```
/birthday set <month> <day>
```
で誕生日を設定します．（通常時設定できるのは１回のみです）

次に，
```
/birthday <true|false>
```
でtrueを選ぶと誕生日の通知及びケーキのプレゼントが有効になります．（初期設定ではfalseになっています．）

誕生日を間違えてしまった場合などは運営に連絡して以下のコマンドを入力してもらうことで変更のロックを1度解除することができます．
```
/birthday reset <MCID>
```

# Commands
プレイヤー用コマンド：
```
/birthday set <month> <day>
/birthday <true|false>
```
オペレーター用コマンド
```
/birthday reset <MCID>
```
