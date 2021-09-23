# Collaborative Whiteboard

This is a shared whiteboard that allows multiple users to draw simultaneously on a canvas. 

## Contributors
<table>
  <tr>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/12579999?v=4" alt="hedgehog7453" style="height: 100px; width:100px;"/></td>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/42616793?v=4" alt="lllrq" style="height: 100px; width:100px; text-align: center;"/></td>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/55377023?v=4" alt="hyperlinnnnk" style="height: 100px; width:100px; text-align: center;"/></td>
    <td align="center"><img src="https://avatars.githubusercontent.com/u/46106051?v=4" alt="kyuif" style="height: 100px; width:100px; text-align: center;"/></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/hedgehog7453">Jiayu Li<br>@hedgehog7453</a></td>
    <td align="center"><a href="https://github.com/lllrq">Ruqi Li<br>@lllrq</a></td>
    <td align="center"><a href="https://github.com/hyperlinnnnk">Ling Fang<br>@hyperlinnnnk</a></td>
    <td align="center"><a href="https://github.com/kyuif">Huiming Su<br>@kyuif</a></td>
  </tr>
</table>

## Functionalities

- Multiple users can draw on a shared canvas concurrently
  - Free drawing and eraser with customisable size
  - Drawing lines, circles, rectangles and ovals
  - Inputting texts
  - Choose colours to draw the above features
![Whiteboard](./img/wb.png)
- Text-based chat window
![chat window](./img/cb.png)
- "File" menu with new, open, save, save as and close (only the manager can control this)

## Running the program

The first user creates a whiteboard and becomes the whiteboard’s manager:

`java -jar ManagerWhiteboard <serverIP> <serverPort>`

Other users can join the whiteboard by inputting server’s IP address and port number:

`java -jar JoinWhiteBoard <serverIP> <serverPort>`
