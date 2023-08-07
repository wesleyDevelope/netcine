package com.example.netcine

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.netcine.model.Movie
import com.example.netcine.util.DownloadImageTask


class MovieAdapter(
    private val movies: List<Movie>,
   @LayoutRes private val layoutId:Int,
   private val onItemClickiListener:( (Int)-> Unit)?  = null
): RecyclerView.Adapter<MovieAdapter.MoviveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId,parent, false)
        return MoviveViewHolder(view)

    }

    override fun onBindViewHolder(holder: MoviveViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MoviveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            val imageCover: ImageView = itemView.findViewById(R.id.img_cover)
            imageCover.setOnClickListener {
                onItemClickiListener?.invoke(movie.id)

            }


            DownloadImageTask(object:DownloadImageTask.Callback{
                override fun onResult(bitmap: Bitmap) {
                    imageCover.setImageBitmap(bitmap)
                }
            }).execute(movie.coverUrl)

            }
           /// Picasso.get().load(movie.coverUrl).into(imageCover)
        }
    }
